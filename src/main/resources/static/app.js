    const {createApp, ref, computed, watch, onMounted} = Vue;

    createApp({
        setup() {
            const API_BASE = '/api/v1';

            const currentTab = ref('designs');
            const threadViewMode = ref('list');

            const threads = ref([]);
            const manufacturers = ref([]);
            const designers = ref([]);
            const designs = ref([]);

            // State tracker for Lazy Loaded Tabs
            const loadedTabs = ref({
                designs: false,
                threads: false,
                designers: false,
                manufacturers: false
            });

            const threadSearch = ref('');
            const selectedManufacturerFilter = ref(null);
            const designSearch = ref('');
            const selectedDesignerFilter = ref(null);

            // Selection Mode & Shopping List State
            const isSelectionMode = ref(false);
            const selectedDesignIds = ref([]);
            const showShoppingModal = ref(false);
            const shoppingList = ref([]);
            const isCalculatingShoppingList = ref(false);

            // Pagination State for Threads (Backend)
            const threadsPage = ref(1);
            const threadsPerPage = ref(15);
            const totalThreadPages = ref(1);

            watch([threadSearch, selectedManufacturerFilter, threadViewMode], () => {
                threadsPage.value = 1;
            });

            const toggleSelectionMode = () => {
                isSelectionMode.value = !isSelectionMode.value;
                if (!isSelectionMode.value) {
                    selectedDesignIds.value = [];
                }
            };

            const toggleDesignSelection = (designId) => {
                const index = selectedDesignIds.value.indexOf(designId);
                if (index > -1) {
                    selectedDesignIds.value.splice(index, 1);
                } else {
                    selectedDesignIds.value.push(designId);
                }
            };

            const toggleSelectAllDesigns = () => {
                if (selectedDesignIds.value.length === filteredDesigns.value.length) {
                    selectedDesignIds.value = [];
                } else {
                    selectedDesignIds.value = filteredDesigns.value.map(d => d.id);
                }
            };

            const calculateShoppingList = async () => {
                if (selectedDesignIds.value.length === 0) return;

                isCalculatingShoppingList.value = true;
                try {
                    const res = await axios.post(`${API_BASE}/designs/shopping-list`, selectedDesignIds.value);
                    shoppingList.value = res.data;
                    showShoppingModal.value = true;
                } catch (err) {
                    showToast('Помилка розрахунку списку закупки', true);
                } finally {
                    isCalculatingShoppingList.value = false;
                }
            };

            const copyShoppingListToClipboard = () => {
                if (shoppingList.value.length === 0) return;

                const textToBuy = shoppingList.value
                    .filter(i => i.toBuyQuantity > 0)
                    .map(i => `${i.manufacturerName} #${i.code} — ${i.toBuyQuantity} шт.`)
                    .join('\n');

                const text = `🛒 Список закупки ниток:\n\n` + (textToBuy || 'Усі нитки є в наявності!');
                navigator.clipboard.writeText(text);
                showToast('Список покупок скопійовано у буфер!');
            };

            // Expansion state for design threads preview
            const expandedDesignIds = ref([]);

            const toggleExpandDesign = (designId) => {
                const index = expandedDesignIds.value.indexOf(designId);
                if (index > -1) {
                    expandedDesignIds.value.splice(index, 1);
                } else {
                    expandedDesignIds.value.push(designId);
                }
            };

            const getVisibleThreads = (design) => {
                if (!design.threads) return [];
                if (expandedDesignIds.value.includes(design.id) || design.threads.length <= 6) {
                    return design.threads;
                }
                return design.threads.slice(0, 6);
            };

            // Notification System
            const toast = ref({show: false, message: '', isError: false});
            const showToast = (message, isError = false) => {
                toast.value = {show: true, message, isError};
                setTimeout(() => toast.value.show = false, 4000);
            };

            // Forms & Modals State
            const threadModal = ref({show: false, isEdit: false, id: null});
            const threadForm = ref({code: '', name: '', manufacturerId: null});

            const manufacturerModal = ref({show: false, isEdit: false, id: null});
            const manufacturerForm = ref({name: ''});

            const designerModal = ref({show: false, isEdit: false, id: null});
            const designerForm = ref({name: ''});

            const designModal = ref({show: false, isEdit: false, id: null});
            const designForm = ref({title: '', designerId: null, url: null, threads: []});
            const selectedImageFile = ref(null);
            const selectedImagePreview = ref(null);
            const imageInput = ref(null);

            const detailsModal = ref({show: false, design: {}});

            // Inventory State
            const showInventoryModal = ref(false);
            const selectedThreadForInventory = ref(null);
            const inventoryMode = ref('add');
            const inventoryForm = ref({
                skeins: 0,
                meters: 0
            });

            // API Calls
            const fetchThreads = async () => {
                try {
                    const res = await axios.get(`${API_BASE}/threads`, {
                        params: {
                            page: threadsPage.value - 1,
                            size: threadsPerPage.value,
                            search: threadSearch.value || undefined,
                            manufacturerId: selectedManufacturerFilter.value || undefined
                        }
                    });
                    threads.value = res.data.content;
                    totalThreadPages.value = res.data.totalPages;
                    loadedTabs.value.threads = true;
                } catch (err) {
                    showToast('Помилка завантаження ниток', true);
                }
            };

            watch(threadsPage, fetchThreads, { immediate: false });

            watch(selectedManufacturerFilter, () => {
                if (loadedTabs.value.threads) {
                    threadsPage.value = 1;
                    fetchThreads();
                }
            });

            let searchTimeout;
            watch(threadSearch, () => {
                if (!loadedTabs.value.threads) return;
                clearTimeout(searchTimeout);
                searchTimeout = setTimeout(() => {
                    threadsPage.value = 1;
                    fetchThreads();
                }, 300);
            });

            const fetchManufacturers = async () => {
                try {
                    const res = await axios.get(`${API_BASE}/manufacturers`);
                    manufacturers.value = res.data;
                    loadedTabs.value.manufacturers = true;
                } catch (err) {
                    showToast('Помилка завантаження виробників', true);
                }
            };

            const fetchDesigners = async () => {
                try {
                    const res = await axios.get(`${API_BASE}/designers`);
                    designers.value = res.data;
                    loadedTabs.value.designers = true;
                } catch (err) {
                    showToast('Помилка завантаження дизайнерів', true);
                }
            };

            const fetchDesigns = async () => {
                try {
                    const response = await axios.get(`${API_BASE}/designs`);
                    designs.value = response.data.content || response.data;
                    loadedTabs.value.designs = true;
                } catch (err) {
                    showToast('Помилка завантаження дизайнів', true);
                }
            };

            // Tab Switcher with Lazy Loading
            const switchTab = (tab) => {
                currentTab.value = tab;
                if (tab === 'threads' && !loadedTabs.value.threads) {
                    fetchThreads();
                } else if (tab === 'designs' && !loadedTabs.value.designs) {
                    fetchDesigns();
                } else if (tab === 'designers' && !loadedTabs.value.designers) {
                    fetchDesigners();
                } else if (tab === 'manufacturers' && !loadedTabs.value.manufacturers) {
                    fetchManufacturers();
                }
            };

            // Custom Searchable Select Methods for Design Form
            const toggleThreadDropdown = (dt) => {
                if (!dt.isOpen && !loadedTabs.value.threads) {
                    fetchThreads();
                }
                dt.isOpen = !dt.isOpen;
            };

            const selectThreadForDesign = (dt, selectedThread) => {
                dt.threadId = selectedThread.id;
                dt.selectedThread = selectedThread;
                dt.isOpen = false;
                dt.searchQuery = '';
            };

            const getFilteredThreadsForSelect = (dt, currentIndex) => {
                const query = (dt.searchQuery || '').toLowerCase();

                const selectedIds = designForm.value.threads
                    .map((item, idx) => idx !== currentIndex ? item.threadId : null)
                    .filter(id => id !== null);

                return allThreads.value.filter(t => {
                    const matchesNotSelected = !selectedIds.includes(t.id);
                    const matchesSearch = t.code.toLowerCase().includes(query) ||
                        t.name.toLowerCase().includes(query);
                    return matchesNotSelected && matchesSearch;
                });
            };

            // Helper methods for calculating thread sufficiency
            const getAvailableMeters = (dt) => {
                if (dt.availableMeters !== undefined && dt.availableMeters !== null) {
                    return dt.availableMeters;
                }
                const threadId = dt.threadId || dt.thread?.id || dt.id;
                const foundThread = allThreads.value.find(t => t.id === threadId);
                return foundThread?.inventory?.totalMeters || 0;
            };

            const isThreadSufficient = (dt) => {
                if (dt.isSufficient !== undefined && dt.isSufficient !== null) {
                    return dt.isSufficient;
                }
                const available = getAvailableMeters(dt);
                const required = dt.requiredMeters || 0;
                return available >= required;
            };

            const isDesignReady = (design) => {
                if (design.canBeStarted !== undefined && design.canBeStarted !== null) {
                    return design.canBeStarted;
                }
                if (!design.threads || design.threads.length === 0) return false;
                return design.threads.every(dt => isThreadSufficient(dt));
            };

            const openDesignDetailsModal = (design) => {
                detailsModal.value = { show: true, design };
            };

            // Designer Actions
            const openDesignerModal = (d = null) => {
                if (d) {
                    designerModal.value = {show: true, isEdit: true, id: d.id};
                    designerForm.value = {name: d.name};
                } else {
                    designerModal.value = {show: true, isEdit: false, id: null};
                    designerForm.value = {name: ''};
                }
            };

            const saveDesigner = async () => {
                try {
                    if (designerModal.value.isEdit) {
                        await axios.put(`${API_BASE}/designers/${designerModal.value.id}`, designerForm.value);
                        showToast('Дизайнера оновлено');
                    } else {
                        await axios.post(`${API_BASE}/designers`, designerForm.value);
                        showToast('Дизайнера додано');
                    }
                    designerModal.value.show = false;
                    fetchDesigners();
                } catch (err) {
                    const msg = err.response?.data?.message || 'Помилка збереження дизайнера';
                    showToast(msg, true);
                }
            };

            const deleteDesigner = async (id) => {
                if (!confirm('Ви дійсно хочете видалити дизайнера?')) return;
                try {
                    await axios.delete(`${API_BASE}/designers/${id}`);
                    showToast('Дизайнера видалено');
                    fetchDesigners();
                } catch (err) {
                    showToast('Неможливо видалити дизайнера, до якого прив\'язані дизайни!', true);
                }
            };

            // Image handling
            const handleImageSelect = (event) => {
                const file = event.target.files[0];
                if (file) {
                    selectedImageFile.value = file;
                    selectedImagePreview.value = URL.createObjectURL(file);
                }
            };

            // Design Actions
            const openDesignModal = async (d = null) => {
                selectedImageFile.value = null;
                selectedImagePreview.value = null;
                if (imageInput.value) imageInput.value.value = '';

                await fetchAllThreads();

                if (!loadedTabs.value.designers) {
                    await fetchDesigners();
                }

                if (d) {
                    designModal.value = {show: true, isEdit: true, id: d.id};
                    designForm.value = {
                        title: d.name || d.title,
                        designerId: d.designer ? d.designer.id : null,
                        url: d.url || null,
                        status: d.status || 'IN_PROGRESS',
                        threads: d.threads ? d.threads.map(t => {
                            const threadObj = allThreads.value.find(item => item.id === (t.threadId || t.thread?.id || t.id));
                            return {
                                threadId: t.threadId || t.thread?.id || t.id,
                                requiredMeters: t.requiredMeters,
                                selectedThread: threadObj || null,
                                isOpen: false,
                                searchQuery: ''
                            };
                        }) : []
                    };
                } else {
                    designModal.value = {show: true, isEdit: false, id: null};
                    designForm.value = {
                        title: '',
                        designerId: designers.value[0]?.id || null,
                        url: null,
                        status: 'IN_PROGRESS',
                        threads: []
                    };
                }
            };

            const addThreadToDesignForm = () => {
                const selectedIds = designForm.value.threads.map(t => t.threadId);
                const availableThread = allThreads.value.find(t => !selectedIds.includes(t.id));

                designForm.value.threads.push({
                    threadId: availableThread ? availableThread.id : null,
                    selectedThread: availableThread || null,
                    requiredMeters: 1.0,
                    isOpen: false,
                    searchQuery: ''
                });
            };

            const removeThreadFromDesignForm = (index) => {
                designForm.value.threads.splice(index, 1);
            };

            const saveDesign = async () => {
                try {
                    const payload = {
                        name: designForm.value.title,
                        designer: designForm.value.designerId,
                        status: designForm.value.status || 'IN_PROGRESS',
                        threads: designForm.value.threads.map(t => ({
                            threadId: t.threadId,
                            requiredMeters: t.requiredMeters
                        }))
                    };

                    let savedDesignResponse;
                    if (designModal.value.isEdit) {
                        savedDesignResponse = await axios.put(`${API_BASE}/designs/${designModal.value.id}`, payload);
                        showToast('Дизайн оновлено');
                    } else {
                        savedDesignResponse = await axios.post(`${API_BASE}/designs`, payload);
                        showToast('Дизайн створено');
                    }

                    const designId = savedDesignResponse.data.id || designModal.value.id;

                    if (selectedImageFile.value && designId) {
                        const formData = new FormData();
                        formData.append('file', selectedImageFile.value);
                        await axios.post(`${API_BASE}/designs/${designId}/image`, formData, {
                            headers: { 'Content-Type': 'multipart/form-data' }
                        });
                    }

                    designModal.value.show = false;
                    fetchDesigns();
                } catch (err) {
                    const msg = err.response?.data?.message || 'Помилка збереження дизайну';
                    showToast(msg, true);
                }
            };

            const deleteDesign = async (id) => {
                if (!confirm('Ви дійсно хочете видалити цей дизайн?')) return;
                try {
                    await axios.delete(`${API_BASE}/designs/${id}`);
                    showToast('Дизайн видалено');
                    fetchDesigns();
                } catch (err) {
                    showToast('Помилка видалення дизайну', true);
                }
            };

            // Thread Actions
            const allThreads = ref([]);

            const fetchAllThreads = async () => {
                try {
                    const res = await axios.get(`${API_BASE}/threads/options`);
                    allThreads.value = res.data;
                } catch (err) {
                    showToast('Помилка завантаження списку ниток', true);
                }
            };

            const openThreadModal = (thread = null) => {
                if (thread) {
                    threadModal.value = {show: true, isEdit: true, id: thread.id};
                    threadForm.value = {
                        code: thread.code,
                        name: thread.name,
                        manufacturerId: thread.manufacturer ? thread.manufacturer.id : null
                    };
                } else {
                    threadModal.value = {show: true, isEdit: false, id: null};
                    threadForm.value = {code: '', name: '', manufacturerId: manufacturers.value[0]?.id || null};
                }
            };

            const saveThread = async () => {
                try {
                    if (threadModal.value.isEdit) {
                        await axios.put(`${API_BASE}/threads/${threadModal.value.id}`, threadForm.value);
                        showToast('Нитку оновлено');
                    } else {
                        await axios.post(`${API_BASE}/threads`, threadForm.value);
                        showToast('Нитку створено');
                    }
                    threadModal.value.show = false;
                    fetchThreads();
                } catch (err) {
                    const msg = err.response?.data?.message || 'Помилка збереження нитки';
                    showToast(msg, true);
                }
            };

            const deleteThread = async (id) => {
                if (!confirm('Ви дійсно хочете видалити цю нитку?')) return;
                try {
                    await axios.delete(`${API_BASE}/threads/${id}`);
                    showToast('Нитку видалено');
                    fetchThreads();
                } catch (err) {
                    showToast('Помилка видалення нитки', true);
                }
            };

            // Manufacturer Actions
            const openManufacturerModal = (m = null) => {
                if (m) {
                    manufacturerModal.value = {show: true, isEdit: true, id: m.id};
                    manufacturerForm.value = {name: m.name};
                } else {
                    manufacturerModal.value = {show: true, isEdit: false, id: null};
                    manufacturerForm.value = {name: ''};
                }
            };

            const saveManufacturer = async () => {
                try {
                    if (manufacturerModal.value.isEdit) {
                        await axios.put(`${API_BASE}/manufacturers/${manufacturerModal.value.id}`, manufacturerForm.value);
                        showToast('Виробника оновлено');
                    } else {
                        await axios.post(`${API_BASE}/manufacturers`, manufacturerForm.value);
                        showToast('Виробника додано');
                    }
                    manufacturerModal.value.show = false;
                    fetchManufacturers();
                } catch (err) {
                    const msg = err.response?.data?.message || 'Помилка збереження виробника';
                    showToast(msg, true);
                }
            };

            const deleteManufacturer = async (id) => {
                if (!confirm('Ви дійсно хочете видалити виробника?')) return;
                try {
                    await axios.delete(`${API_BASE}/manufacturers/${id}`);
                    showToast('Виробника видалено');
                    fetchManufacturers();
                } catch (err) {
                    showToast('Неможливо видалити виробника: до нього прив\'язані нитки!', true);
                }
            };

            // Inventory Actions
            const openInventoryModal = (thread, mode = 'add') => {
                selectedThreadForInventory.value = thread;
                inventoryMode.value = mode;

                if (mode === 'add') {
                    inventoryForm.value = { skeins: 1, meters: 0 };
                } else {
                    inventoryForm.value = {
                        skeins: thread.inventory?.skeinsQuantity || 0,
                        meters: thread.inventory?.bobbinMeters || 0
                    };
                }
                showInventoryModal.value = true;
            };

            const saveInventory = async () => {
                try {
                    const endpoint = inventoryMode.value === 'add'
                        ? `${API_BASE}/inventory/add`
                        : `${API_BASE}/inventory/update`;

                    await axios.post(endpoint, {
                        threadId: selectedThreadForInventory.value.id,
                        addSkeins: inventoryForm.value.skeins,
                        addBobbinMeters: inventoryForm.value.meters
                    });

                    showInventoryModal.value = false;
                    showToast(inventoryMode.value === 'add' ? 'Запас поповнено' : 'Залишки оновлено');
                    await fetchThreads();
                    if (loadedTabs.value.designs) {
                        await fetchDesigns();
                    }
                } catch (e) {
                    showToast('Помилка збереження інвентарю', true);
                }
            };

            // Computed Filters
            const filteredDesigns = computed(() => {
                return designs.value.filter(d => {
                    const nameMatch = !designSearch.value ||
                        (d.name && d.name.toLowerCase().includes(designSearch.value.toLowerCase())) ||
                        (d.title && d.title.toLowerCase().includes(designSearch.value.toLowerCase()));

                    const designerMatch = !selectedDesignerFilter.value ||
                        (d.designer && d.designer.id === selectedDesignerFilter.value) ||
                        (d.designerId === selectedDesignerFilter.value);

                    return nameMatch && designerMatch;
                });
            });

            // Helper for smart page buttons with ellipsis (...)
            const getVisiblePages = (currentPage, totalPages) => {
                if (totalPages <= 7) {
                    return Array.from({ length: totalPages }, (_, i) => i + 1);
                }
                if (currentPage <= 4) {
                    return [1, 2, 3, 4, 5, '...', totalPages];
                }
                if (currentPage >= totalPages - 3) {
                    return [1, '...', totalPages - 4, totalPages - 3, totalPages - 2, totalPages - 1, totalPages];
                }
                return [1, '...', currentPage - 1, currentPage, currentPage + 1, '...', totalPages];
            };

            // Only load data for the active initial tab (designs)
            onMounted(() => {
                fetchDesigns();
            });

            return {
                currentTab,
                switchTab,
                threads,
                manufacturers,
                designers,
                designs,
                threadSearch,
                selectedManufacturerFilter,
                designSearch,
                selectedDesignerFilter,
                filteredDesigns,
                threadsPage,
                totalThreadPages,
                getVisiblePages,
                isSelectionMode,
                selectedDesignIds,
                showShoppingModal,
                shoppingList,
                isCalculatingShoppingList,
                toggleSelectionMode,
                toggleDesignSelection,
                toggleSelectAllDesigns,
                calculateShoppingList,
                copyShoppingListToClipboard,
                expandedDesignIds,
                toggleExpandDesign,
                getVisibleThreads,
                toast,
                threadModal,
                threadForm,
                manufacturerModal,
                manufacturerForm,
                designerModal,
                designerForm,
                designModal,
                designForm,
                selectedImageFile,
                selectedImagePreview,
                imageInput,
                handleImageSelect,
                detailsModal,
                openDesignDetailsModal,
                getAvailableMeters,
                isThreadSufficient,
                isDesignReady,
                openThreadModal,
                saveThread,
                deleteThread,
                openManufacturerModal,
                saveManufacturer,
                deleteManufacturer,
                openDesignerModal,
                saveDesigner,
                deleteDesigner,
                openDesignModal,
                addThreadToDesignForm,
                removeThreadFromDesignForm,
                saveDesign,
                deleteDesign,
                showInventoryModal,
                selectedThreadForInventory,
                inventoryMode,
                inventoryForm,
                openInventoryModal,
                saveInventory,
                threadViewMode,
                toggleThreadDropdown,
                selectThreadForDesign,
                getFilteredThreadsForSelect,
                allThreads,
                fetchAllThreads
            };
        }
    }).mount('#app');

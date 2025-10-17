(function () {
    const container = document.getElementById('cards');
    const controls = document.getElementById('controls');

    const fetchTasks = async () => {
        try {
            const url = '/api/task/all' + buildQuery();
            const res = await fetch(url);
            if (!res.ok) throw new Error(`HTTP ${res.status}`);
            return await res.json();
        } catch (e) {
            console.error(e);
            showInfo('Ошибка загрузки данных. Проверьте, что сервер запущен и доступен по /api/task/all');
            return null;
        }
    };

    const fetchTaskDetails = async (extNumber) => {
        try {
            const res = await fetch(`/api/task?ext_number=${encodeURIComponent(extNumber)}`);
            if (res.status === 404) throw new Error('Заявка не найдена');
            if (!res.ok) throw new Error(`HTTP ${res.status}`);
            return await res.json();
        } catch (e) {
            console.error(e);
            throw e;
        }
    };

    const showInfo = (text) => {
        container.innerHTML = '';
        const div = document.createElement('div');
        div.className = 'info';
        div.textContent = text;
        container.appendChild(div);
    };

    // Формататоры дат оставлены для вспомогательных нужд (цвет бейджа по плановой дате),
    // но показываем значения дат так, как они приходят из API.
    const getTodayISODate = () => {
        const now = new Date();
        const y = now.getFullYear();
        const m = `${now.getMonth() + 1}`.padStart(2, '0');
        const d = `${now.getDate()}`.padStart(2, '0');
        return `${y}-${m}-${d}`;
    };

    const getPriorityClass = (priority) => {
        if (priority === 'Нормальный') return 'badge-green';
        if (priority === 'Высокий') return 'badge-red';
        return 'badge-none';
    };

    const getStatusClass = (status) => {
        switch (status) {
            case 'Закрыта': return 'badge-green';
            case 'Отмена': return 'badge-red';
            case 'В работе': return 'badge-orange';
            case 'В обработке': return 'badge-blue';
            default: return 'badge-none';
        }
    };

    const getPlanDateClass = (planISODate) => {
        if (!planISODate) return 'badge-none';
        const today = getTodayISODate();
        if (planISODate === today) return 'badge-orange';
        if (planISODate < today) return 'badge-red';
        return 'badge-none';
    };

    const makeEl = (tag, className, text) => {
        const el = document.createElement(tag);
        if (className) el.className = className;
        if (text !== undefined) el.textContent = text;
        return el;
    };

    const createBadge = (text, badgeClass) => {
        const b = makeEl('div', `badge ${badgeClass || 'badge-none'}`);
        b.textContent = text ?? '-';
        return b;
    };

    const buildDescription = (task) => {
        const parts = [
            task.type,
            task.name_type,
            task.count
        ].filter(v => v !== null && v !== undefined && `${v}`.trim().length > 0);
        return parts.join('\n');
    };

    // ===== Панель управления: сбор параметров =====
    const getCheckedValues = (name) => {
        return Array.from(controls.querySelectorAll(`input[name="${name}"]`))
            .filter(i => i.type === 'checkbox' && i.checked)
            .map(i => i.value);
    };

    const getRadioValue = (name) => {
        const el = controls.querySelector(`input[name="${name}"]:checked`);
        return el ? el.value : null;
    };

    const buildQuery = () => {
        const params = new URLSearchParams();

        // Приоритет (список через запятую)
        const priorities = getCheckedValues('priority');
        if (priorities.length === 0) {
            params.set('priority', '');
        } else {
            params.set('priority', priorities.join(','));
        }

        // Статус (список через запятую)
        const statuses = getCheckedValues('status');
        if (statuses.length === 0) {
            params.set('status', '');
        } else {
            params.set('status', statuses.join(','));
        }

        // План выполнения (today | future | overdue)
        const planType = getRadioValue('plan_date_type') || 'today';
        params.set('plan_date_type', planType);

        // Дата выполнения (YYYY-MM-DD)
        const dateStart = document.getElementById('date_start').value;
        const dateEnd = document.getElementById('date_end').value;
        if (dateStart) params.set('date_start', dateStart);
        if (dateEnd) params.set('date_end', dateEnd);

        // Поиск
        const find = document.getElementById('find').value.trim();
        if (find.length > 0) params.set('find', find);

        // Лимит
        const limit = document.getElementById('limit').value;
        params.set('limit', limit);

        // Сортировка по (date | journal | id)
        const orderType = document.getElementById('order_type').value;
        params.set('order_type', orderType);

        // Тип сортировки (true | false)
        const sortAsc = getRadioValue('sort_asc') || 'false';
        params.set('sort_asc', sortAsc);

        const qs = params.toString();
        return qs ? `?${qs}` : '';
    };

    // Обработчики: теперь обновление ТОЛЬКО по кнопке (submit формы)
    const attachControlEvents = () => {
        controls.addEventListener('submit', (e) => {
            e.preventDefault();
            applyFilters();
        });
    };

    const applyFilters = async () => {
        const data = await fetchTasks();
        if (data) render(data);
    };

    // ===== Модалка деталей =====
    const modal = document.getElementById('modal');
    const modalBackdrop = document.getElementById('modal-backdrop');
    const modalCloseBtn = document.getElementById('modal-close');
    const modalTitleExt = document.getElementById('modal-title-ext');
    const modalBody = document.getElementById('modal-body');

    const openModal = () => {
        modal.classList.remove('hidden');
        modal.setAttribute('aria-hidden', 'false');
        document.body.style.overflow = 'hidden';
    };

    const closeModal = () => {
        modal.classList.add('hidden');
        modal.setAttribute('aria-hidden', 'true');
        document.body.style.overflow = '';
        modalBody.innerHTML = '';
    };

    modalBackdrop.addEventListener('click', closeModal);
    modalCloseBtn.addEventListener('click', closeModal);
    window.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') closeModal();
    });

    const fieldLabels = [
        ['id', 'ID'],
        ['ext_number', 'Внешний номер'],
        ['number_journal', 'Номер журнала'],
        ['type', 'Тип'],
        ['priority', 'Приоритет'],
        ['name_type', 'Наименование типа'],
        ['location_repair', 'Место ремонта'],
        ['lokation_repair', 'Место ремонта'],
        ['count', 'Количество'],
        ['drawing_number', 'Номер чертежа'],
        ['page_count', 'Кол-во страниц'],
        ['full_name', 'ФИО'],
        ['division', 'Подразделение'],
        ['tg_id', 'Telegram ID'],
        ['code_users', 'Код пользователя'],
        ['email', 'Email'],
        ['phone', 'Телефон'],
        ['status', 'Статус'],
        ['comment', 'Комментарий'],
        ['application_date', 'Дата заявки'],
        ['comment_ready', 'Комментарий о готовности'],
        ['date_ready', 'Дата готовности'],
        ['comments_closing', 'Комментарий о закрытии'],
        ['closing_date', 'Дата закрытия'],
        ['photo_1', 'Фото 1'],
        ['photo_2', 'Фото 2'],
        ['photo_3', 'Фото 3'],
        ['comment_shift', 'Комментарий смены'],
        ['plan_complete_date', 'План. дата выполнения'],
        ['plan_complet_date', 'План. дата выполнения'],
        ['category_change', 'Категория изменения'],
        ['amos_order_number', 'Номер заказа AMOS'],
        ['department_ogm', 'Отдел ОГМ'],
    ];

    const isUrl = (s) => typeof s === 'string' && /^(https?:\/\/|\/|data:)/i.test(s);

    const maybeFormatDate = (val) => {
        if (val === null || val === undefined) return '-';
        return String(val);
    };

    const renderDetails = (task) => {
        const grid = makeEl('div', 'details-grid');

        fieldLabels.forEach(([key, label]) => {
            if (!(key in task) || task[key] === null || task[key] === undefined || `${task[key]}`.length === 0) return;

            const v = task[key];

            const lab = makeEl('div', 'details-label', label);
            const val = makeEl('div', 'details-value');

            if (['photo_1', 'photo_2', 'photo_3'].includes(key) && isUrl(v)) {
                const a = document.createElement('a');
                a.href = v;
                a.target = '_blank';
                a.rel = 'noopener noreferrer';
                a.textContent = 'Открыть';
                val.appendChild(a);
            } else {
                val.textContent = maybeFormatDate(v);
            }

            grid.appendChild(lab);
            grid.appendChild(val);
        });

        modalBody.innerHTML = '';
        modalBody.appendChild(grid);
    };

    const openDetails = async (extNumber) => {
        modalTitleExt.textContent = extNumber != null ? `#${extNumber}` : '';
        modalBody.innerHTML = '<div class="info">Загрузка...</div>';
        openModal();
        try {
            const task = await fetchTaskDetails(extNumber);
            renderDetails(task);
        } catch (e) {
            modalBody.innerHTML = `<div class="info">${e.message || 'Ошибка загрузки'}</div>`;
        }
    };

    // ===== Рендер карточек =====
    const createCard = (task) => {
        const card = makeEl('div', 'request-card');

        const left = makeEl('div', 'left-section');

        // id | Номер в журнале
        {
            const group = makeEl('div', 'field-group');
            group.appendChild(makeEl('div', 'field-label', 'id | Номер в журнале'));
            const val = makeEl('div', 'field-value');
            const ext = task.ext_number ?? '-';
            const journal = task.number_journal ?? '-';
            const numText = `${ext} | ${journal}`;
            val.appendChild(createBadge(numText, 'badge-none'));
            group.appendChild(val);
            left.appendChild(group);
        }

        left.appendChild(makeEl('div', 'divider'));

        // Приоритет
        {
            const group = makeEl('div', 'field-group');
            group.appendChild(makeEl('div', 'field-label', 'Приоритет'));
            const val = makeEl('div', 'field-value');
            val.appendChild(createBadge(task.priority ?? '-', getPriorityClass(task.priority)));
            group.appendChild(val);
            left.appendChild(group);
        }

        left.appendChild(makeEl('div', 'divider'));

        // Дата подачи — отображаем как приходит из API
        {
            const group = makeEl('div', 'field-group');
            group.appendChild(makeEl('div', 'field-label', 'Дата подачи'));
            const val = makeEl('div', 'field-value');
            val.appendChild(createBadge(task.application_date ?? '-', 'badge-none'));
            group.appendChild(val);
            left.appendChild(group);
        }

        left.appendChild(makeEl('div', 'divider'));

        // Дата выполнения — как в API, для цвета используем сравнение ISO-дат
        {
            const group = makeEl('div', 'field-group');
            group.appendChild(makeEl('div', 'field-label', 'Дата выполнения'));
            const val = makeEl('div', 'field-value');
            const planISO = task.plan_complete_date || null;
            val.appendChild(createBadge(planISO ? planISO : '-', getPlanDateClass(planISO)));
            group.appendChild(val);
            left.appendChild(group);
        }

        const vdiv = makeEl('div', 'vertical-divider');

        const right = makeEl('div', 'right-section');

        // Описание — растягивается и имеет внутреннюю прокрутку
        {
            const group = makeEl('div', 'field-group full-height scrollable');
            group.appendChild(makeEl('div', 'description-label', 'Описание'));
            const desc = makeEl('div', 'description-text');
            desc.textContent = buildDescription(task);
            group.appendChild(desc);
            right.appendChild(group);
        }

        right.appendChild(makeEl('div', 'divider'));

        // Комментарий — тоже с внутренней прокруткой
        {
            const group = makeEl('div', 'field-group scrollable');
            group.appendChild(makeEl('div', 'description-label', 'Комментарий'));
            const commentEl = makeEl('div', 'description-text comment-text');
            commentEl.textContent = task.comment && `${task.comment}`.trim().length > 0 ? task.comment : '-';
            group.appendChild(commentEl);
            right.appendChild(group);
        }

        right.appendChild(makeEl('div', 'divider'));

        // Статус
        {
            const group = makeEl('div', 'field-group');
            group.appendChild(makeEl('div', 'field-label', 'Статус'));
            const val = makeEl('div', 'field-value');
            val.appendChild(createBadge(task.status ?? '-', getStatusClass(task.status)));
            group.appendChild(val);
            right.appendChild(group);
        }

        // Клик по карточке -> детали
        card.addEventListener('click', () => {
            const ext = task.ext_number;
            if (ext !== null && ext !== undefined) openDetails(ext);
        });

        card.appendChild(left);
        card.appendChild(vdiv);
        card.appendChild(right);

        return card;
    };

    const render = (tasks) => {
        container.innerHTML = '';
        if (!tasks || tasks.length === 0) {
            showInfo('Заявок не найдено');
            return;
        }
        tasks.forEach(task => container.appendChild(createCard(task)));
    };

    (async function init() {
        attachControlEvents();
        const data = await fetchTasks();
        if (data) render(data);
    })();
})();
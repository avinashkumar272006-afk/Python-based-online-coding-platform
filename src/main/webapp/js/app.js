/**
 * Python-Based Online Coding Platform
 * Minimal Vanilla JavaScript UI Controller
 * 
 * Safely handles:
 * - Mobile responsive sidebar toggle (when present)
 * - Accessible keyboard navigation (Escape to close mobile sidebar)
 * - Defensive conversation scroll preservation (when present)
 * 
 * Completely decoupled from server logic; gracefully no-ops when elements are absent.
 */

(function () {
    'use strict';

    function initApp() {
        initSidebarToggle();
        initConversationAutoScroll();
    }

    /**
     * Mobile sidebar drawer toggle with backdrop overlay.
     * Safely does nothing if sidebar elements do not exist in the DOM.
     */
    function initSidebarToggle() {
        var toggleBtn = document.querySelector('.sidebar-toggle');
        var sidebar = document.querySelector('.app-sidebar');
        var overlay = document.querySelector('.sidebar-overlay');

        if (!toggleBtn || !sidebar) {
            return;
        }

        function openSidebar() {
            sidebar.classList.add('open');
            if (overlay) {
                overlay.classList.add('active');
            }
            toggleBtn.setAttribute('aria-expanded', 'true');
        }

        function closeSidebar() {
            sidebar.classList.remove('open');
            if (overlay) {
                overlay.classList.remove('active');
            }
            toggleBtn.setAttribute('aria-expanded', 'false');
        }

        toggleBtn.addEventListener('click', function (event) {
            event.stopPropagation();
            if (sidebar.classList.contains('open')) {
                closeSidebar();
            } else {
                openSidebar();
            }
        });

        if (overlay) {
            overlay.addEventListener('click', closeSidebar);
        }

        // Close drawer on Escape key for accessibility
        document.addEventListener('keydown', function (event) {
            if (event.key === 'Escape' && sidebar.classList.contains('open')) {
                closeSidebar();
            }
        });
    }

    /**
     * Preserves auto-scroll behavior on chat / conversation boxes if present.
     */
    function initConversationAutoScroll() {
        var box = document.getElementById('conversationBox');
        if (box) {
            box.scrollTop = box.scrollHeight;
        }
    }

    // Attach listener when DOM is fully parsed
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initApp);
    } else {
        initApp();
    }
})();

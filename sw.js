/**
 * Neonatal Nursing LMS - Offline Service Worker (sw.js)
 * Enables complete offline learning, course studying, and quiz taking.
 */

const CACHE_NAME = 'neonatal-lms-v2';
const STATIC_ASSETS = [
  './',
  './index.html',
  'https://cdn.tailwindcss.com',
  'https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&family=JetBrains+Mono:wght@400;500&display=swap'
];

// INSTALL: Pre-cache application shell
self.addEventListener('install', (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME).then((cache) => {
      return cache.addAll(STATIC_ASSETS).catch((err) => {
        console.warn('Pre-caching non-fatal asset issue:', err);
      });
    }).then(() => self.skipWaiting())
  );
});

// ACTIVATE: Clean up older cache versions
self.addEventListener('activate', (event) => {
  event.waitUntil(
    caches.keys().then((keys) => {
      return Promise.all(
        keys.filter((key) => key !== CACHE_NAME).map((key) => caches.delete(key))
      );
    }).then(() => self.clients.claim())
  );
});

// FETCH: Network-First with Cache Fallback for App Shell and Assets
self.addEventListener('fetch', (event) => {
  const req = event.request;
  const url = new URL(req.url);

  // Do not intercept non-GET requests (POSTs to Google Apps Script are handled by IndexedDB sync queue)
  if (req.method !== 'GET') {
    return;
  }

  // Google Scripts execution endpoint - let network handle, fallback handled by app IndexedDB
  if (url.hostname.includes('script.google.com') || url.hostname.includes('script.googleusercontent.com')) {
    return;
  }

  // Network First, fallback to cache
  event.respondWith(
    fetch(req)
      .then((networkResponse) => {
        // Cache successful responses for offline use
        if (networkResponse && networkResponse.status === 200) {
          const resClone = networkResponse.clone();
          caches.open(CACHE_NAME).then((cache) => cache.put(req, resClone));
        }
        return networkResponse;
      })
      .catch(() => {
        // When offline, serve from cache
        return caches.match(req).then((cachedResponse) => {
          if (cachedResponse) {
            return cachedResponse;
          }
          // If requesting an HTML navigation while offline, return cached index.html
          if (req.headers.get('accept')?.includes('text/html')) {
            return caches.match('./index.html') || caches.match('./');
          }
          return new Response('Offline: Content unavailable without network connection.', {
            status: 503,
            statusText: 'Service Unavailable',
            headers: new Headers({ 'Content-Type': 'text/plain' })
          });
        });
      })
  );
});

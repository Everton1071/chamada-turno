// Guarda o app no aparelho para abrir mesmo sem internet.
const CACHE = 'chamada-turno-v7';
const ARQUIVOS = ['./', './index.html', './manifest.json', './icon-192.png', './icon-512.png',
  'https://cdnjs.cloudflare.com/ajax/libs/xlsx/0.18.5/xlsx.full.min.js'];
self.addEventListener('install', e => e.waitUntil(caches.open(CACHE).then(c => c.addAll(ARQUIVOS)).then(() => self.skipWaiting())));
self.addEventListener('activate', e => e.waitUntil(caches.keys().then(ks => Promise.all(ks.filter(k => k !== CACHE).map(k => caches.delete(k)))).then(() => self.clients.claim())));
self.addEventListener('fetch', e => {
  if (e.request.method !== 'GET') return;
  // rede primeiro (pega atualizações); se estiver sem internet, usa a cópia guardada
  // arquivos do próprio app: sempre confere com o servidor se há versão nova (não usa a cópia de até 10 min do navegador)
  const doApp = new URL(e.request.url).origin === self.location.origin;
  e.respondWith(fetch(e.request, doApp ? {cache:'no-cache'} : undefined).then(r => { const copia = r.clone(); caches.open(CACHE).then(c => c.put(e.request, copia)).catch(() => {}); return r; })
    .catch(() => caches.match(e.request).then(r => r || caches.match('./index.html'))));
});

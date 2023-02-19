import { createApp } from 'vue';
import { createPinia } from 'pinia';
import App from './App.vue';
import router from './router';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap'; // Javascript
import './assets/main.css';
import axios from 'axios';

const server_url =  import.meta.env.VITE_BASE_URL;
console.log('server-URL ' + server_url);
axios.defaults.baseURL = server_url; // FIXME aus env nehmen

// Pinia State Management (ersetzt Vuex)
const pinia = createPinia()
const app = createApp(App)

app.use(router)
app.use(pinia)

app.mount('#app')

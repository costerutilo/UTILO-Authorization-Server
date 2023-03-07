import { createApp } from 'vue';
import { createPinia } from 'pinia';
import App from './App.vue';
import router from './router';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap'; // Javascript
import './assets/css/main.css';
import axios from 'axios';
import { createI18n } from 'vue-i18n';

const server_url =  import.meta.env.VITE_BASE_URL;
console.log('server-URL ' + server_url);
axios.defaults.baseURL = server_url; // FIXME aus env nehmen

// Pinia State Management (ersetzt Vuex)
const pinia = createPinia();

const messages = {
    en: {
        message: {
            navInfo: 'Info',
            navClients: 'Clients',
            headline: 'UTILO Authorization Server',
            edit: 'edit',
            theWelcomeHeadline: 'Administration',
            theWelcomeDesc: 'This application is only accessible to authorized users.',
            theWelcomeVersion: 'Version',
            theWelcomeProfile: 'Profile',
            loginHeadline: 'login',
            clientListHeadline: 'Client List',
            clientAuthenticationMethods: 'Client authentication methods',
            authorizationGrantTypes: 'Authorization grant types',
            redirectUris: 'Redirect uris',
            scopes: 'Scopes',
            confirmDeleteClient: 'Are you sure to delete the client {msg}?'
        }
    },
    de: {
        message: {
            navInfo: 'Info',
            navClients: 'Clients',
            headline: 'UTILO Authorization Server',
            edit: 'ändern',
            theWelcomeHeadline: 'Administration',
            theWelcomeDesc: 'Diese Anwendung ist nur für autorisierte Benutzer zugänglich.',
            theWelcomeVersion: 'Version',
            theWelcomeProfile: 'Profil',
            loginHeadline: 'Login',
            clientListHeadline: 'Client Liste',
            clientAuthenticationMethods: 'Client Authentifizierungs Methoden',
            authorizationGrantTypes: 'Autorisation Grant Typen',
            redirectUris: 'Redirect URIs',
            scopes: 'Scopes',
            confirmDeleteClient: 'Wollen Sie den Client {msg} wirklich löschen?'
        }
    }
}
const i18n = createI18n({
    // something vue-i18n options here ...
    locale: 'de', // set locale
    fallbackLocale: 'en', // set fallback locale
    messages, // set locale messages
    // If you need to specify other options, you can set other options
    // ...
})
const app = createApp(App);

app.use(i18n);
app.use(router);
app.use(pinia);

app.mount('#app');

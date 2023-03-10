import { defineStore } from 'pinia'

// You can name the return value of `defineStore()` anything you want,
// but it's best to use the name of the store and surround it with `use`
// and `Store` (e.g. `useUserStore`, `useCartStore`, `useProductStore`)
// the first argument is a unique id of the store across your application
export const useAuthorizationStore = defineStore('authorization', {
  // other options...
  state: {

    // showAreas: null,
    oauthCode: null

  },
  mutations: {

    // setShowAreas(state, showAreas) {
    //   state.showAreas = showAreas
    // },
    setOauthCode(state, oauthCode) {
      state.oauthCode = oauthCode;
    }

  },
  getters: {

    // showAreas:                                          state => state.showAreas,
    oauthCode:  state => state.oauthCode

  }

});
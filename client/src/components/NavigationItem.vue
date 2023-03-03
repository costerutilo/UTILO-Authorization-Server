<template>

  <nav class="navbar navbar-expand-lg navbar-light bg-light">

    <div class="container">

      <a class="navbar-brand" href="/">
        <img :src="'src/assets/logo/' + logo"
             id="logo"
             class="d-inline-block align-top"
             :alt="logoAlt">
      </a>

      <button class="navbar-toggler"
              type="button"
              data-bs-toggle="collapse"
              data-bs-target="#navbarSupportedContent"
              aria-controls="navbarSupportedContent"
              aria-expanded="false"
              aria-label="Navigation anzeigen/ausblenden">
        <span class="navbar-toggler-icon"></span>
      </button>

      <div class="collapse navbar-collapse" id="navbarSupportedContent">

        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
          <li class="nav-item">
            <RouterLink class="nav-link" to="/about">Infos</RouterLink>
          </li>
        </ul>
        <form class="form-inline my-2 my-lg-0">
          <RouterLink class="btn btn-outline-primary my-2 my-sm-0" to="/login">Login</RouterLink>
        </form>

      </div>

    </div>

  </nav>

</template>

<script setup lang="ts">

  // logo aus config
  const logo: string = import.meta.env.VITE_LOGO;
  const logoAlt: string = import.meta.env.VITE_LOGO_ALT;

</script>

<style scoped>

  #logo {
    max-height: 70px;
  }

</style>

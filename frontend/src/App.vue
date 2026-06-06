<template>
  <div id="app">
    <NavBar />
    <router-view />
  </div>
</template>

<script setup>
import NavBar from './components/NavBar.vue'
</script>

<style>
#app {
  min-height: 100vh;
  background: #f5f5f5;
}
</style>

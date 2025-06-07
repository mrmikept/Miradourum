<template>
  <div id="app">
    <router-view /> <!-- For Vue Router -->
    <!-- OR -->
    <!-- <MyComponent /> -->
  </div>
</template>

<script>
export default {
  name: 'App',
};
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body, #app {
  width: 100%;
  height: 100%;
  font-family: Avenir, Helvetica, Arial, sans-serif;
  background-color: white; /* Match your home-page */
}
</style>

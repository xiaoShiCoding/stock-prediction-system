<template>
    <div id="app">
        <router-view></router-view>
    </div>
</template>

<script>
export default {
    name: "App",
    components: {},
};
</script>

<style>
*{
    padding: 0;
    margin: 0;
}
#app {
    min-width: 1000px;
    min-height: 500px;
}
</style>
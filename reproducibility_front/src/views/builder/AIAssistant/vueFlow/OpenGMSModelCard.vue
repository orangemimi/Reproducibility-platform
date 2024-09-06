<template>
  <div>
    <!-- component -->
    <div
      class="relative flex cursor-pointer w-full max-w-[48rem] flex-row rounded-xl bg-white bg-clip-border text-gray-700 border hover:border-indigo-300 antialiased"
      style="text-align: left"
      @click="openModel"
    >
      <div
        class="relative m-0 w-2/5 shrink-0 overflow-hidden rounded-xl rounded-r-none bg-white bg-clip-border text-gray-700"
      >
        <img
          src="http://112.4.132.6:8083/data/f67c1b01-4e34-4839-bfa6-51f9419bc4f6"
          alt="image"
          class="object-cover"
        />
      </div>
      <div class="p-6">
        <h6
          class="mb-4 block font-sans text-sm font-semibold uppercase leading-relaxed tracking-normal text-dark-500 antialiased"
        >
          {{
            props.modelInfo.name
              ? props.modelInfo.name
              : "Please wait for the inquiry to end"
          }}
        </h6>
        <h4
          class="mb-2 block font-sans text-sm leading-snug tracking-normal font-light text-gray-700 text-justify antialiased"
        >
          {{
            props.modelInfo.overview
              ? props.modelInfo.overview
              : "Please wait for the inquiry to end"
          }}
        </h4>
      </div>
    </div>

    <!-- stylesheet -->
    <link
      rel="stylesheet"
      href="https://unpkg.com/@material-tailwind/html@latest/styles/material-tailwind.css"
    />
  </div>
</template>
  
  <script lang="ts" setup>
import { defineProps } from "vue";
// @ts-ignore
import { modelQuery } from "@/api/request.js";

const props = defineProps({
  modelInfo: {
    type: Object,
    required: true,
  },
});

const openModel = () => {
  if (props.modelInfo.url) {
    window.open(props.modelInfo.url, "_blank");
  }
};
</script>
  <style scoped></style>
  
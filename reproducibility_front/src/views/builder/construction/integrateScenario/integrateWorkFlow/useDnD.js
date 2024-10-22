import { Label } from "@icon-park/vue-next";
import { useVueFlow } from "@vue-flow/core";
import { ref, watch } from "vue";

let id = 0;

/**
 * @returns {string} - A unique id.
 */
function getId() {
  return Math.random().toString();
}

/**
 * In a real world scenario you'd want to avoid creating refs in a global scope like this as they might not be cleaned up properly.
 * @type {{draggedType: Ref<string|null>, isDragOver: Ref<boolean>, isDragging: Ref<boolean>}}
 */
const state = {
  /**
   * The type of the node being dragged.
   */
  draggedType: ref(null),
  isDragOver: ref(false),
  isDragging: ref(false),
  draggedInfo: ref({}),
};

export default function useDragAndDrop() {
  const { draggedType, isDragOver, isDragging, draggedInfo } = state;

  const { addNodes, screenToFlowCoordinate, onNodesInitialized, updateNode } =
    useVueFlow();

  watch(isDragging, (dragging) => {
    document.body.style.userSelect = dragging ? "none" : "";
  });

  function onDragStart(event, nodeInfo, type) {
    if (event.dataTransfer) {
      event.dataTransfer.setData("application/vueflow", type);
      event.dataTransfer.effectAllowed = "move";
    }

    draggedType.value = type;
    isDragging.value = true;
    draggedInfo.value = nodeInfo;
    document.addEventListener("drop", onDragEnd);
  }

  /**
   * Handles the drag over event.
   *
   * @param {DragEvent} event
   */
  function onDragOver(event) {
    event.preventDefault();

    if (draggedType.value) {
      isDragOver.value = true;

      if (event.dataTransfer) {
        event.dataTransfer.dropEffect = "move";
      }
    }
  }

  function onDragLeave() {
    isDragOver.value = false;
  }

  function onDragEnd() {
    isDragging.value = false;
    isDragOver.value = false;
    draggedType.value = null;
    document.removeEventListener("drop", onDragEnd);
  }

  /**
   * Handles the drop event.
   *
   * @param {DragEvent} event
   */
  function onDrop(event) {
    const position = screenToFlowCoordinate({
      x: event.clientX,
      y: event.clientY,
    });

    const nodeId = getId();
    let newNode;

    if (draggedInfo.value) {
      newNode = {
        id: nodeId,
        type: draggedType.value,
        position,
        data: {
          label: draggedInfo.value?.name,
          description: draggedInfo.value?.description,
          behavior: {
            inputs: draggedInfo.value?.behavior[0].inputs,
            outputs: draggedInfo.value?.behavior[0].outputs,
            parameters: draggedInfo.value?.behavior[0].parameters,
          },
          state: "init",
        },
      };
    } else {
      newNode = {
        id: nodeId,
        type: draggedType.value,
        position,
        data: {
          label: draggedType.value,
          description: "No Description",
          behavior: {
            inputs: [],
            outputs: [],
            parameters: [],
          },
          state: "init",
        },
      };
    }

    /**
     * Align node position after drop, so it's centered to the mouse
     *
     * We can hook into events even in a callback, and we can remove the event listener after it's been called.
     */
    // const { off } = onNodesInitialized(() => {
    //   updateNode(nodeId, (node) => ({
    //     position: {
    //       x: node.position.x - node.dimensions.width / 2,
    //       y: node.position.y - node.dimensions.height / 2,
    //     },
    //   }));

    //   off();
    // });

    // addNodes(newNode);
    return newNode;
  }

  return {
    draggedType,
    isDragOver,
    isDragging,
    onDragStart,
    onDragLeave,
    onDragOver,
    onDrop,
  };
}

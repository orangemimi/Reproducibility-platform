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
          md5: draggedInfo.value?.md5,
          behavior: {
            inputs: draggedInfo.value?.behavior[0].inputs,
            outputs: draggedInfo.value?.behavior[0].outputs,
            parameters: draggedInfo.value?.behavior[0].parameters,
            name: draggedInfo.value?.behavior[0].name,
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

// {
//   "outputs": [
//     {
//       "template": {
//         "type": "id",
//         "value": "F73F31FF-2F23-4C7A-A57D-39D0C7A6C4E6"
//       },
//       "statename": "WatershedDelienationState",
//       "event": "MaskDEM"
//     },
//     {
//       "template": {
//         "type": "id",
//         "value": "F73F31FF-2F23-4C7A-A57D-39D0C7A6C4E6"
//       },
//       "statename": "WatershedDelienationState",
//       "event": "FillDEM"
//     },
//     {
//       "template": {
//         "type": "id",
//         "value": "F73F31FF-2F23-4C7A-A57D-39D0C7A6C4E6"
//       },
//       "statename": "WatershedDelienationState",
//       "event": "D8"
//     },
//     {
//       "template": {
//         "type": "id",
//         "value": "F73F31FF-2F23-4C7A-A57D-39D0C7A6C4E6"
//       },
//       "statename": "WatershedDelienationState",
//       "event": "D8Slope"
//     },
//     {
//       "template": {
//         "type": "id",
//         "value": "F73F31FF-2F23-4C7A-A57D-39D0C7A6C4E6"
//       },
//       "statename": "WatershedDelienationState",
//       "event": "DInf"
//     },
//     {
//       "template": {
//         "type": "id",
//         "value": "F73F31FF-2F23-4C7A-A57D-39D0C7A6C4E6"
//       },
//       "statename": "WatershedDelienationState",
//       "event": "DInfSlope"
//     },
//     {
//       "template": {
//         "type": "id",
//         "value": "F73F31FF-2F23-4C7A-A57D-39D0C7A6C4E6"
//       },
//       "statename": "WatershedDelienationState",
//       "event": "AreaD8"
//     },
//     {
//       "template": {
//         "type": "id",
//         "value": "F73F31FF-2F23-4C7A-A57D-39D0C7A6C4E6"
//       },
//       "statename": "WatershedDelienationState",
//       "event": "AreaDInf"
//     },
//     {
//       "template": {
//         "type": "id",
//         "value": "4996E027-209B-4121-907B-1ED36A417D22"
//       },
//       "statename": "WatershedDelienationState",
//       "event": "OrignalStreamShape"
//     },
//     {
//       "template": {
//         "type": "id",
//         "value": "F73F31FF-2F23-4C7A-A57D-39D0C7A6C4E6"
//       },
//       "statename": "WatershedDelienationState",
//       "event": "Watershed"
//     },
//     {
//       "template": {
//         "type": "id",
//         "value": "F73F31FF-2F23-4C7A-A57D-39D0C7A6C4E6"
//       },
//       "statename": "WatershedDelienationState",
//       "event": "WatershedGrid"
//     },
//     {
//       "template": {
//         "type": "id",
//         "value": "F73F31FF-2F23-4C7A-A57D-39D0C7A6C4E6"
//       },
//       "statename": "WatershedDelienationState",
//       "event": "WatershedShape"
//     },
//     {
//       "template": {
//         "type": "none",
//         "value": ""
//       },
//       "statename": "HydroResponseUnitState",
//       "event": "HydroResponseUnitData"
//     },
//     {
//       "template": {
//         "type": "id",
//         "value": "4996E027-209B-4121-907B-1ED36A417D22"
//       },
//       "statename": "HydroResponseUnitState",
//       "event": "FullHruShape"
//     },
//     {
//       "template": {
//         "type": "none",
//         "value": ""
//       },
//       "statename": "RunSWATState",
//       "event": "SWATRUN"
//     },
//     {
//       "template": {
//         "type": "none",
//         "value": ""
//       },
//       "statename": "SWATBasinShapeGenerator",
//       "event": "SWATBasinShapeGen"
//     }
//   ],
//   "port": "8061",
//   "inputs": [
//     {
//       "statename": "WatershedDelienationState",
//       "event": "DEM",
//       "url": "http://111.229.14.128:8082/dataResource/getResource?sourceStoreId=22aabb2b-50f9-420b-a366-05b790fdf83e",
//       "tag": "udx_zip_dem",
//       "suffix": "zip"
//     },
//     {
//       "statename": "WatershedDelienationState",
//       "event": "MaskShape",
//       "url": "http://111.229.14.128:8082/dataResource/getResource?sourceStoreId=b862bc0c-4694-42bb-b649-637d4158bdef",
//       "tag": "udx_zip_mask",
//       "suffix": "zip"
//     },
//     {
//       "statename": "WatershedDelienationState",
//       "event": "ThresholdValue",
//       "url": "http://111.229.14.128:8082/dataResource/getResource?sourceStoreId=38d6a13c-ce04-4b60-b2a9-0753fe94d6fb",
//       "tag": "ThresholdValue",
//       "suffix": "xml",
//       "children": [
//         {
//           "eventId": "deleneationThreshold",
//           "eventName": "deleneationThreshold",
//           "eventDesc": "deleneationThreshold",
//           "eventType": "INT",
//           "child": true,
//           "value": "7200"
//         }
//       ]
//     },
//     {
//       "statename": "WatershedDelienationState",
//       "event": "OriginalOutletShape",
//       "url": "http://111.229.14.128:8082/dataResource/getResource?sourceStoreId=e2e906e3-98e1-4f30-9f2c-b05109b6c861",
//       "tag": "udx_zip_outlet",
//       "suffix": "zip"
//     },
//     {
//       "statename": "WatershedDelienationState",
//       "event": "SnapThresholdValue",
//       "url": "http://111.229.14.128:8082/dataResource/getResource?sourceStoreId=b958fe59-97e1-48da-8fc0-c95e93b6654a",
//       "tag": "SnapThresholdValue",
//       "suffix": "xml",
//       "children": [
//         {
//           "eventId": "threshold",
//           "eventName": "threshold",
//           "eventDesc": "threshold",
//           "eventType": "REAL",
//           "child": true,
//           "value": "300"
//         }
//       ]
//     },
//     {
//       "statename": "HydroResponseUnitState",
//       "event": "Landuse",
//       "url": "http://111.229.14.128:8082/dataResource/getResource?sourceStoreId=507b7278-d14a-4458-b6b2-53a3e8b09b94",
//       "tag": "udx_zip_landuse",
//       "suffix": "zip"
//     },
//     {
//       "statename": "HydroResponseUnitState",
//       "event": "Soil",
//       "url": "http://111.229.14.128:8082/dataResource/getResource?sourceStoreId=5b38c6e4-cff6-4281-968d-0f8a23626001",
//       "tag": "udx_zip_soil",
//       "suffix": "zip"
//     },
//     {
//       "statename": "HydroResponseUnitState",
//       "event": "Station",
//       "url": "http://111.229.14.128:8082/dataResource/getResource?sourceStoreId=9637f4d1-3b7a-401a-88df-8f560946ca00",
//       "tag": "station",
//       "suffix": "txt"
//     },
//     {
//       "statename": "HydroResponseUnitState",
//       "event": "PCP",
//       "url": "http://111.229.14.128:8082/dataResource/getResource?sourceStoreId=45b6564b-27b4-4bee-b13c-8638b0bdba2c",
//       "tag": "765850",
//       "suffix": "pcp"
//     },
//     {
//       "statename": "HydroResponseUnitState",
//       "event": "WeatherGenerator",
//       "url": "http://111.229.14.128:8082/dataResource/getResource?sourceStoreId=03ea9eaf-57e9-429f-9de2-6f1f672ba6c7",
//       "tag": "generator",
//       "suffix": "wgn"
//     },
//     {
//       "statename": "HydroResponseUnitState",
//       "event": "TMP",
//       "url": "http://111.229.14.128:8082/dataResource/getResource?sourceStoreId=9473ace3-4ca7-4f87-af17-b262b5319737",
//       "tag": "765850",
//       "suffix": "tmp"
//     },
//     {
//       "statename": "HydroResponseUnitState",
//       "event": "MinumParameter",
//       "url": "http://111.229.14.128:8082/dataResource/getResource?sourceStoreId=9b959a52-2cea-46ba-a816-04da3235d3eb",
//       "tag": "MinumParameter",
//       "suffix": "xml",
//       "children": [
//         {
//           "eventId": "minCrop",
//           "eventName": "minCrop",
//           "eventDesc": "minCrop",
//           "eventType": "REAL",
//           "child": true,
//           "value": "20"
//         },
//         {
//           "eventId": "minSoil",
//           "eventName": "minSoil",
//           "eventDesc": "minSoil",
//           "eventType": "REAL",
//           "child": true,
//           "value": "10"
//         },
//         {
//           "eventId": "minSlope",
//           "eventName": "minSlope",
//           "eventDesc": "minSlope",
//           "eventType": "REAL",
//           "child": true,
//           "value": "5"
//         }
//       ]
//     },
//     {
//       "statename": "HydroResponseUnitState",
//       "event": "TimeSpan",
//       "url": "http://111.229.14.128:8082/dataResource/getResource?sourceStoreId=c16d66f3-c5f7-46e4-b09a-3fe9f3d04365",
//       "tag": "TimeSpan",
//       "suffix": "xml",
//       "children": [
//         {
//           "eventId": "year_start",
//           "eventName": "year_start",
//           "eventDesc": "year_start",
//           "eventType": "INT",
//           "child": true,
//           "value": "2000"
//         },
//         {
//           "eventId": "month_start",
//           "eventName": "month_start",
//           "eventDesc": "month_start",
//           "eventType": "INT",
//           "child": true,
//           "value": "1"
//         },
//         {
//           "eventId": "day_start",
//           "eventName": "day_start",
//           "eventDesc": "day_start",
//           "eventType": "INT",
//           "child": true,
//           "value": "1"
//         },
//         {
//           "eventId": "year_end",
//           "eventName": "year_end",
//           "eventDesc": "year_end",
//           "eventType": "INT",
//           "child": true,
//           "value": "2001"
//         },
//         {
//           "eventId": "month_end",
//           "eventName": "month_end",
//           "eventDesc": "month_end",
//           "eventType": "INT",
//           "child": true,
//           "value": "12"
//         },
//         {
//           "eventId": "day_end",
//           "eventName": "day_end",
//           "eventDesc": "day_end",
//           "eventType": "INT",
//           "child": true,
//           "value": "31"
//         }
//       ]
//     },
//     {
//       "statename": "HydroResponseUnitState",
//       "event": "SlopeLimitation",
//       "url": "http://111.229.14.128:8082/dataResource/getResource?sourceStoreId=1205102f-8714-43c4-9f8f-2c7815a72835",
//       "tag": "SlopeLimitation",
//       "suffix": "xml",
//       "children": [
//         {
//           "eventId": "slope_limitation",
//           "eventName": "slope_limitation",
//           "eventDesc": "slope_limitation",
//           "eventType": "INT_ARRAY",
//           "child": true,
//           "value": "10"
//         }
//       ]
//     }
//   ],
//   "ip": "127.0.0.1",
//   "pid": "069e1d69934bdf131cc5b38825c7990d",
//   "oid": "16e31602-bd05-4da4-bd01-cb7582c21ae8",
//   "username": "xukai"
// }

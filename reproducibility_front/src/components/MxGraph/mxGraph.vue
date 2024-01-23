<template>
  <div class="main">
    <!-- <div class="leftBar" v-if="role == 'builder'">
          <left-toolbar ref="leftToolbar"></left-toolbar>
        </div> -->

    <div class="mainContainer">
      <div class="modelbarTop">
        <el-divider direction="vertical"></el-divider>

        <el-dropdown trigger="click" @command="zoom">
          <el-button link size="default">
            {{ size }}%
            <el-icon><ArrowDownBold /></el-icon>
          </el-button>
          <template v-slot:dropdown>
            <el-dropdown-menu>
              <el-dropdown-item :command="50">50%</el-dropdown-item>
              <el-dropdown-item :command="75">75%</el-dropdown-item>
              <el-dropdown-item :command="100">100%</el-dropdown-item>
              <el-dropdown-item :command="125">125%</el-dropdown-item>
              <el-dropdown-item :command="150">150%</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <el-button @click="importGraphDialog = true" link size="default"
          >Import</el-button
        >

        <el-button @click="saveMx" link size="default">Save</el-button>
        <el-button
          @click="graphLayout(true, 'hierarchicalLayout')"
          link
          size="default"
          >Suit</el-button
        >
        <el-button @click="savePic" link size="default"
          >save as pic</el-button
        >
      </div>

      <perfect-scrollbar style="height: 600px">
        <div
          id="fullScreenComponent"
          class="graphContainer"
          ref="container"
          @contextmenu.prevent
        ></div>
      </perfect-scrollbar>
    </div>
    <div class="dialogs">
      <el-dialog v-model="importGraphDialog" width="60%">
        <el-input
          type="textarea"
          :rows="24"
          placeholder="请输入内容"
          v-model="importGraphText"
        >
        </el-input>
        <div style="display: flex; justify-content: flex-end">
          <el-button type="primary" @click="importGraph" style="margin: 20px"
            >Save</el-button
          >
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script>
// import bus from "./components/bus";
import bus from "vue3-eventbus";
import mxgraph from "./components/index";
import { genGraph } from "./components/initMx.js";

const {
  mxUtils,
  mxCodec,
  mxEvent,
  mxHierarchicalLayout,
  mxParallelEdgeLayout,
  mxCompactTreeLayout,
  mxCircleLayout,
  mxMorphing,
  mxConstants,
  // mxCellState,
} = mxgraph;

export default {
  props: ["expectedInstances"],
  watch: {
    expectedInstances() {
      this.graph.removeCells(
        this.graph.getChildVertices(this.graph.getDefaultParent())
      );
      this.initInstance();
      // this.mxContent += `</root></mxGraphModel>`;
      this.graph.importGraph(this.mxContent);
      this.graphLayout(true, "hierarchicalLayout");
      // this.getDocument();
    },
  },
  data() {
    return {
      graph: null,
      selectionCells: [],
      modelListInGraph: [],
      modelInputInGraph: [],
      modelLinkInGraph: [], //下一模型的输入数据
      modelOutputInGraph: [],
      linkEdgeList: [],
      dataItemList: [],

      ops: {
        bar: {
          onlyShowBarOnScroll: false,
          keepShow: true,
          background: "#c1c1c1",
          opacity: 1,
          hoverStyle: false,
          specifyBorderRadius: false,
          minSize: 0,
          size: "6px",
          disable: false,
        },
        rail: {
          keepShow: true,
        },
      },

      container: null,
      mxContent:
        '<mxGraphModel><root><mxCell id="0"/><mxCell id="1" parent="0"/>',
      // mxContentEnd: "</root></mxGraphModel>",
      index: 2,
      modelDoubleClick: false,
      modelClick: false,
      dataClick: false,
      // codeDoubleClick:false,
      currentCell: null,
      dataDoubleClick: false,
      importGraphDialog: false,
      importGraphText: "",
      fullscreenFlag: false,
      size: 100,
      procedureContent: "",
    };
  },
  methods: {
    zoom(val) {
      this.size = val;
      this.graph.zoomTo(val / 100);
    },
    zoomIn() {
      this.size = this.size + 10;
      this.graph.zoomTo(this.size / 100);
    },
    zoomOut() {
      this.size = this.size - 10;
      this.graph.zoomTo(this.size / 100);
    },
    importGraph() {
      this.importGraphDialog = false;
      this.graph.importGraph(this.importGraphText);
    },

    saveMx() {
      // const codec = new mxCodec();
      // const encodedModel = codec.encode(this.graph.getModel());
      // const xml = mxUtils.getXml(encodedModel);
      // console.log(xml);
    },
    getSelectionCells() {
      // $on(bus, "go", (data) => {
      //   this.selectionCells = data;
      // });
      bus.on("go", (data) => {
        this.selectionCells = data;
      });
    },

    savePic() {
      this.exportFile("png");
      //设置直线
    },
    graphLayout(animate, layoutType) {
      this.graph.getModel().beginUpdate();
      try {
        if (layoutType === "randomLayout") {
          // 随机布局
          mxParallelEdgeLayout.prototype.minDistanceLimit = 50;
          // eslint-disable-next-line new-cap
          var layout = new mxParallelEdgeLayout(this.graph);
          layout.forceConstant = 500;
          layout.execute(this.graph.getDefaultParent());
        } else if (layoutType === "hierarchicalLayout") {
          // 分层布局
          mxHierarchicalLayout.prototype.intraCellSpacing = 10;
          mxHierarchicalLayout.prototype.fineTuning = false;
          mxHierarchicalLayout.prototype.traverseAncestors = true;
          mxHierarchicalLayout.prototype.resizeParent = true;

          // 无关系实体之间的间距
          mxHierarchicalLayout.prototype.interHierarchySpacing = 50;
          // 层级之间的距离
          mxHierarchicalLayout.prototype.interRankCellSpacing = 50;
          // console.log(mxHierarchicalLayout.prototype, "101");

          // eslint-disable-next-line new-cap
          var hierarchicallayout = new mxHierarchicalLayout(
            this.graph,
            mxConstants.DIRECTION_NORTH
          );
          hierarchicallayout.execute(this.graph.getDefaultParent());
        } else if (layoutType === "compactTreeLayout") {
          // 树形布局
          // eslint-disable-next-line new-cap
          var compactTreelayout = new mxCompactTreeLayout(this.graph);
          compactTreelayout.execute(this.graph.getDefaultParent());
        } else if (layoutType === "circleLayout") {
          // 圆形布局
          // eslint-disable-next-line new-cap
          var circleLayout = new mxCircleLayout(this.graph, 400);
          circleLayout.execute(this.graph.getDefaultParent());
        }
      } finally {
        // 是否开启布局动画
        if (animate) {
          // eslint-disable-next-line new-cap
          var morph = new mxMorphing(this.graph, 20, 7.7, 20);
          morph.addListener(mxEvent.DONE, () => {
            this.graph.getModel().endUpdate();
          });
          morph.startAnimation();
        } else {
          this.graph.getModel().endUpdate();
        }
      }
    },

    // importModelXML(xmlTxt) {
    //   //xml to json
    //   this.graph.getModel().beginUpdate();
    //   try {
    //     const doc = mxUtils.parseXml(xmlTxt);
    //     const root = doc.documentElement;
    //     const dec = new mxCodec(root.ownerDocument);
    //     dec.decode(root, this.graph.getModel());
    //   } finally {
    //     this.graph.getModel().endUpdate();
    //   }
    //   // this._restoreModel();
    // },

    //--------------初始化 bar的modelItem的内容--由 AllModels组件返回
    initInstance() {
      let getExpectedInstances = this.expectedInstances;
      if (getExpectedInstances == undefined) {
        return;
      }
      let x = 1;
      let y = 1;

      getExpectedInstances.forEach((model) => {
        model.index = this.index;
        this.modelListInGraph.push({
          name: model.name,
          type: "model",
          id: this.index,
          style:
            "fillColor=#07689f;fontColor=#f6f6f6;shape=hexagon;fixedSize=1;strokeWidth=1.5;align=center;imageAlign=center;imageVerticalAlign=top;whiteSpace=wrap;",
          mxGeometry: {
            x: x * 220,
            y: y * 200,
          },
          md5: model.id,
        });
        // y = y + 1;
        x = 1;
        this.index += 1;

        // let judgeParameters = service.behavior.parameters;
        // let xInInput = 1;
        // let yy = 0;
        // console.log(service,'service');
        model.behavior.forEach((behavior) => {
          behavior.inputs.forEach((input) => {
            // x = 1;
            // let style = "fillColor=#fff8f8;fontColor=#24292E;shape=parallelogram;fixedSize=1;strokeWidth=1.5;align=center;imageAlign=center;imageVerticalAlign=top";
            let addItem = {
              name: input.name,
              type: "input",
              value: input.value,
              id: this.index,
              style:
                "fillColor=#fff8f8;fontColor=#e43a3a;shape=parallelogram;fixedSize=1;strokeWidth=1.5;align=center;imageAlign=center;imageVerticalAlign=top;whiteSpace=wrap;",
              optional: input.isOptional,
              mxGeometry: {
                x: x * 200,
                y: y * 200 - 50,
              },

              md5: model.id,
              modelId: model.index,
            };
            // let yy = 0;
            if (input.isOptional == true) {
              this.dataItemList.push(addItem);
            } else {
              addItem.style =
                "fillColor=#fff8f8;fontColor=#b7685a;shape=parallelogram;fixedSize=1;strokeWidth=1.5;align=center;imageAlign=center;imageVerticalAlign=top;whiteSpace=wrap;";
              this.dataItemList.push(addItem);
            }

            this.index += 1;
            x += 1;
            // xInInput = x;
            // yy += 1;
          });

          if (behavior.parameters != undefined) {
            x = 1;

            // y = y + 1;
            behavior.parameters.forEach((parameter) => {
              // let x = 1;
              this.dataItemList.push({
                name: parameter.name,
                type: "parameter",
                value: parameter.value,
                id: this.index,
                style:
                  "fillColor=#ffcc99;fontColor=#b7685a;shape=parallelogram;fixedSize=1;strokeWidth=1.5;align=center;imageAlign=center;imageVerticalAlign=top",
                optional: "true",
                mxGeometry: {
                  x: x * 200,
                  y: y * 200 - 100,
                },

                modelId: model.index,
              });
              this.index += 1;
              x += 1;

              // xInInput = x;
            });
          }
          x = 1;
          // y = y + 1;
          behavior.outputs.forEach((output) => {
            // let x = 1;
            this.dataItemList.push({
              name: output.name,
              type: "output",
              value: output.value,
              id: this.index,
              style:
                "fillColor=#b9e6d3;fontColor=#24292E;shape=parallelogram;fixedSize=1;strokeWidth=1.5;align=center;imageAlign=center;imageVerticalAlign=top",
              optional: output.isOptional,
              mxGeometry: {
                x: x * 200,
                y: y * 200 + 50,
              },

              modelId: model.index,
            });
            this.index += 1;
            x += 1;
            // x = xInInput;
          });
          y += 1;
        });

        // x++;
        y += 1;
      });

      let dataItems = this.dataItemList;
      // console.log(this.dataItemList,'this.dataItemList');

      //遍历每个实例的输出与非自身实例的输入是否存在数据引用关系，存在则s生成Link
      let inputs = this.dataItemList.filter((data) => data.type == "input");
      let outputs = this.dataItemList.filter((data) => data.type == "output");

      dataItems.forEach((data) => {
        // console.log(data,'1110');
        if (data.type == "output") {
          this.linkEdgeList.push({
            id: this.index,
            parent: 1,
            source: data.modelId,
            target: data.id,
            edge: "1",
          });
          this.index++;
        } else {
          this.linkEdgeList.push({
            id: this.index,
            parent: 1,
            source: data.id,
            target: data.modelId,
            edge: "1",
          });
          this.index++;
        }
      });

      outputs.forEach((output) => {
        // console.log(output,'1101');
        inputs.forEach((input) => {
          // console.log(input,'11201');
          if (output.value === input.value) {
            // debugger;
            output.type = "intermediate";
            output.style =
              "fillColor=#f4d160;fontColor=#24292E;shape=parallelogram;fixedSize=1;strokeWidth=1.5;align=center;imageAlign=center;imageVerticalAlign=top";
            this.linkEdgeList.push({
              id: this.index,
              source: output.id,
              target: input.id,
              edge: "1",
            });
            this.index++;
          }
        });
      });

      this.generateMxXml();
      this.graphLayout(true, "hierarchicalLayout");
      this.importGraphText = this.mxContent;
    },

    //监听事件
    listenGraphEvent() {
      // 监听双击事件
      this.graph.addListener(mxEvent.DOUBLE_CLICK, async (graph, evt) => {
        // DOUBLE_CLICK
        if (
          evt.properties.cell != undefined &&
          evt.properties.cell.type != undefined
        ) {
          let cell = evt.properties.cell;
          let clickModelType = cell.type;

          if (clickModelType == "model") {
            // console.log(cell);
            this.currentCell = cell;
            this.modelDoubleClick = true;
            // await initSetTimeOut();
            this.dataDoubleClick = this.dataClick = this.modelClick = false;
          } else {
            this.modelDoubleClick = this.modelClick = this.dataClick = false;

            this.currentCell = cell;
            this.dataDoubleClick = true;
          }
        }
      });

      // 监听单击事件
      //单击空白处 dialog隐藏
      this.graph.addListener(mxEvent.CLICK, (sender, evt) => {
        let isCell = Object.prototype.hasOwnProperty.call(
          evt.properties,
          "cell"
        );
        if (isCell) {
          let cell = evt.properties.cell;
          this.graph.addSelectionCell(cell);
          // $emit(bus, "go", this.graph.getSelectionCells());
          bus.emit("go", this.graph.getSelectionCells());
          const clickModelType = cell.type;

          if (clickModelType == "modelService") {
            // 使用 mxGraph 事件中心，触发自定义事件
            // this.currentCell = cell;
            this.modelClick = true;
            this.modelDoubleClick =
              this.dataClick =
              this.dataDoubleClick =
              this.codeClick =
                false;
          } else if (clickModelType == "code") {
            this.codeClick = true;
            this.modelDoubleClick =
              this.dataClick =
              this.dataDoubleClick =
              this.modelClick =
                false;
          } else {
            this.modelDoubleClick =
              this.modelClick =
              this.dataDoubleClick =
              this.codeClick =
                false;

            // console.log(cell);
            this.dataNode = cell;
            this.dataClick = true;
          }
        } else {
          //单击空白处
          this.currentCell = {};
          // console.log(this.graph.getSelectionCells())
          // $emit(bus, "go", []);
          bus.emit("go", []);
        }
      });
    },
    //     getDocument() {
    //       let getExpectedInstances = this.expectedInstances;
    //       this.procedureContent = `<AgentInfo>
    // <Add name="xxx" title="Mr." type="Creator" email="xxx@xxx" insititution="xxxx"/>
    // </AgentInfo>
    // <Configuration>`;
    //       getExpectedInstances.forEach((model) => {
    //         this.procedureContent += `<TaskInstance id="${model.id}" name="${model.name}" description="" modelServiceId="${service.id}"><Behavior>`;
    //         model.behavior.forEach((behavior) => {
    //           this.procedureContent += "<Behavior><Inputs>";
    //           behavior.inputs.forEach((input) => {
    //             this.procedureContent += ` <Input name="${input.name}" isOptional="${input.isOptional}" dataServiceId="${input.value}"/>`;
    //           });

    //           if (behavior.parameters != undefined) {
    //             this.procedureContent += `<Parameters>`;
    //             behavior.parameters.forEach((parameter) => {
    //               this.procedureContent += `<Parameter name="${parameter.name}" type="${parameter.type}" value="${parameter.value}"/>`;
    //             });
    //             this.procedureContent += `</Parameters>`;
    //           }
    //           this.procedureContent += `<Outputs>`;
    //           behavior.outputs.forEach((output) => {
    //             this.procedureContent += `<Output name="${output.name}" isOptional="${output.isOptional}" dataServiceId="${output.value}"/>`;
    //           });
    //         });

    //         this.procedureContent += `</Outputs></Behavior>`;
    //       });
    //       this.procedureContent += ` </Behaviors></TaskInstance></Configuration>`;
    //       console.log(this.procedureContent);
    //     },

    generateMxXml() {
      let dataItemList = this.dataItemList;
      let modelItemList = this.modelListInGraph;
      let linkList = this.linkEdgeList;
      // let content = this.mxContent;
      modelItemList.forEach((data) => {
        this.mxContent += ` <mxCell id= "${data.id}" style="${data.style}" parent="1" vertex="1" name="${data.name}" value="${data.name}" type="model">
<mxGeometry x= "${data.mxGeometry.x}" y="${data.mxGeometry.y}" width="200" height="50" as="geometry" />
</mxCell>`;
      });
      dataItemList.forEach((data) => {
        this.mxContent += ` <mxCell id= "${data.id}" style="${data.style}" parent="1" vertex="1" name="${data.name}" value="${data.name}" type="data">
<mxGeometry x= "${data.mxGeometry.x}" y="${data.mxGeometry.y}" width="200" height="50" as="geometry" />
</mxCell>`;
      });
      linkList.forEach((data) => {
        this.mxContent += ` <mxCell id= "${data.id}" parent="1" source="${data.source}" target="${data.target}" edge="1" ><mxGeometry relative="1" as="geometry"/></mxCell>`;
      });
      this.mxContent += `</root></mxGraphModel>`;
    },
    //初始化mxgraph
    async init() {
      //创建画布

      this.container = this.$refs.container;
      this.createGraph();

      this.graph.setPanning(true);
    },

    // async getScenario() {
    //   let data = await getScenariosByProjectId(this.projectId);

    //   this.selectId = data.selectTaskId;
    //   let flag = data.selectTaskId == this.currentTask.id;
    //   this.isSelectTaskInConsruction = flag;

    // },

    // Creates the graph inside the given container
    createGraph() {
      this.graph = genGraph(this.container);
    },
  },
  mounted() {
    this.init();
    this.listenGraphEvent();
  },
  emits: ["go"],
};
</script>

<style lang="scss" scoped>
.main {
  /*// width: 100%;*/ /*// height: 100%;*/ /*// display: flex;*/ /*// position: relative;*/
  width: 100%;
  .mainContainer {
    float: left;
    // position: relative;
    // top: 0;
    // left: 300px;
    width: calc(100%);
    height: 100%;
    .modelbarTop {
      background: rgb(251, 251, 251);
      padding-left: 10px;
      border-radius: 4px;
      margin-bottom: 5px;

      .fade-enter-active,
      .fade-leave-active {
        transition: opacity 0.3s ease;
      }
      .fade-enter, .fade-leave-to
/* .component-fade-leave-active for below version 2.1.8 */ {
        opacity: 0;
      }

      .bell {
        float: left;
        color: rgb(224, 75, 75);
        margin: 0 8px;
        font-size: 20px;
      }

      .bell:hover {
        cursor: pointer;
        transition: all 0.2s ease-in-out;
      }
    }
    .graphContainer {
      position: relative;
      overflow: hidden;
      height: 100%;
      width: 100%;
      min-width: calc(100%);
      min-height: 80vh;
      // background: rgb(251, 251, 251) url("./mxgraph/point.gif") 0 0 repeat;
      border-radius: 4px;
    }
  }

  .outline-wrapper {
    border: 1px solid #dedede;
    background: #fff;
    position: fixed;
    right: 30px;
    bottom: 40px;
    border-radius: 4px;
    z-index: 10;
    > h4 {
      padding: 6px;
      font-size: 12px;
      border-bottom: 1px solid #e6e6e6;
    }
    #graphOutline {
      width: 200px;
    }
  }
}
</style>

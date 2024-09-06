<template>
  <textarea></textarea>
</template>

<script>
// import { ref, onMounted, onBeforeUnmount, onBeforeUpdate, defineEmits } from 'vue';
import { $emit } from "../../utils/gogocodeTransfer";
import CodeMirror from "codemirror/lib/codemirror"; // 导入 CodeMirror

import "codemirror/lib/codemirror.css";
import "codemirror/addon/display/fullscreen.css";
import "codemirror/addon/display/fullscreen.js";
import "codemirror/theme/base16-dark.css";
import "codemirror/theme/blackboard.css";
import "codemirror/addon/hint/show-hint.css";
import "codemirror/addon/hint/show-hint.js";
import "codemirror/addon/hint/show-hint";
import "codemirror/addon/hint/javascript-hint";
import "codemirror/addon/hint/sql-hint";
import "codemirror/mode/javascript/javascript";
import "codemirror/mode/markdown/markdown";
import "codemirror/mode/sql/sql";
import "codemirror/mode/php/php";
import "codemirror/mode/python/python";
import "codemirror/mode/shell/shell";
import "codemirror/mode/powershell/powershell";
export default {
  data() {
    return {
      content: "",
    };
  },
  props: {
    hint: Boolean,
    code: String,
    value: String,
    unseenLines: Array,
    marker: Function,
    options: {
      type: Object,
      default: function () {
        return {
          styleActiveLine: true,
          lineNumbers: true,
          mode: "python",
          lineWrapping: true,
          theme: "blackboard",
          hint: "python",
        };
      },
    },
  },
  created() {
    if (!this.options) {
      this.options = {};
    }
    // this.options = this.options || {};
    var language = this.options.mode || "python";
    var theme = this.options.theme;
    // 保存所有 import Promise 的数组
    const importPromises = [];

    // require active-line.js
    if (this.options.styleActiveLine)
      importPromises.push(import("codemirror/addon/selection/active-line.js"));

    // require closebrackets.js
    if (this.options.autoCloseBrackets)
      importPromises.push(import("codemirror/addon/edit/closebrackets.js"));

    // require closetag.js
    if (this.options.autoCloseTags)
      importPromises.push(import("codemirror/addon/edit/closetag.js"));

    // require styleSelectedText.js
    if (this.options.styleSelectedText) {
      importPromises.push(
        import("codemirror/addon/selection/mark-selection.js")
      );
      importPromises.push(import("codemirror/addon/search/searchcursor.js"));
    }

    // highlightSelectionMatches
    if (this.options.highlightSelectionMatches) {
      importPromises.push(
        import("codemirror/addon/scroll/annotatescrollbar.js")
      );
      importPromises.push(
        import("codemirror/addon/search/matchesonscrollbar.js")
      );
      importPromises.push(import("codemirror/addon/search/searchcursor.js"));
      importPromises.push(
        import("codemirror/addon/search/match-highlighter.js")
      );
    }

    // require emacs
    if (
      !!this.options.keyMap &&
      ["emacs", "sublime", "vim"].indexOf(this.options.keyMap) > -1
    ) {
      importPromises.push(import("codemirror/mode/clike/clike.js"));
      importPromises.push(import("codemirror/addon/edit/matchbrackets.js"));
      importPromises.push(import("codemirror/addon/comment/comment.js"));
      importPromises.push(import("codemirror/addon/dialog/dialog.js"));
      importPromises.push(import("codemirror/addon/dialog/dialog.css"));
      importPromises.push(import("codemirror/addon/search/searchcursor.js"));
      importPromises.push(import("codemirror/addon/search/search.js"));
      // console.log(this.options.keyMap)
      importPromises.push(
        import(
          /* @vite-ignore */ "codemirror/keymap/" + this.options.keyMap + ".js"
        )
      );
    }

    // require fold js
    if (this.options.foldGutter) {
      importPromises.push(import("codemirror/addon/fold/foldgutter.css"));
      importPromises.push(import("codemirror/addon/fold/brace-fold.js"));
      importPromises.push(import("codemirror/addon/fold/comment-fold.js"));
      importPromises.push(import("codemirror/addon/fold/foldcode.js"));
      importPromises.push(import("codemirror/addon/fold/foldgutter.js"));
      importPromises.push(import("codemirror/addon/fold/indent-fold.js"));
      importPromises.push(import("codemirror/addon/fold/markdown-fold.js"));
      importPromises.push(import("codemirror/addon/fold/xml-fold.js"));
    }

    // require language mode config
    // importPromises.push(import(/* @vite-ignore */"codemirror/mode/" + language + "/" + language + ".js"));

    // require theme config
    if (!!theme && theme == "solarized light") theme = "solarized";
    if (!!theme && theme != "default")
      //   importPromises.push(import(/* @vite-ignore */"codemirror/theme/" + theme + ".css"));

      // 使用 Promise.all 确保所有的 import 完成
      Promise.all(importPromises)
        .then(() => {
          // 所有 import 完成后的处理
        })
        .catch((error) => {
          console.error("Error during dynamic imports:", error);
        });
  },
  mounted() {
    this.editor = CodeMirror.fromTextArea(this.$el, this.options);
    this.editor.setValue(this.code || this.value || this.content);
    this.editor.on("change", (cm) => {
      this.content = cm.getValue();
      this.$emit("changed", this.content);
      // this.$emit("input", this.content);
    });
    this.unseenLineMarkers();
  },
  watch: {
    code(newVal) {
      this.handleCodeValueChange(newVal);
    },
    value(newVal) {
      this.handleCodeValueChange(newVal);
    },
  },
  methods: {
    handleCodeValueChange(newVal) {
      const editorValue = this.editor ? this.editor.getValue() : "";
      if (newVal !== editorValue) {
        const scrollInfo = this.editor.getScrollInfo();
        this.editor.setValue(newVal);
        this.content = newVal;
        this.editor.scrollTo(scrollInfo.left, scrollInfo.top);
      }
      this.unseenLineMarkers();
    },
    unseenLineMarkers() {
      var _this = this;
      if (_this.unseenLines !== undefined && _this.marker !== undefined) {
        _this.unseenLines.forEach((line) => {
          var info = _this.editor.lineInfo(line);
          _this.editor.setGutterMarker(
            line,
            "breakpoints",
            info.gutterMarkers ? null : _this.marker()
          );
        });
      }
    },
  },
  emits: ["changed", "update:value"],
};
</script>

<style>
.CodeMirror-code {
  line-height: 1.6em;
  font-family: Menlo, Monaco, Consolas, "Courier New", monospace;
}
.CodeMirror {
  height: 100%;
}
</style>

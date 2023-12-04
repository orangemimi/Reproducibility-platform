<!-- 这个组件是为了回避vue3+vite升级后的问题
    vite使用ES6，不支持commonjs
-->
<template>
  <textarea></textarea>
</template>

<script>
import { ref, onMounted, onBeforeUnmount, onBeforeUpdate, defineEmits } from 'vue';
import { $emit } from '../../utils/gogocodeTransfer'
// var CodeMirrorMetas = require("./metas.js");
import CodeMirror from 'codemirror/lib/codemirror'; // 导入 CodeMirror
import 'codemirror/lib/codemirror.css';
import 'codemirror/addon/display/fullscreen.css';
import 'codemirror/addon/display/fullscreen.js';
import 'codemirror/theme/base16-dark.css'
import 'codemirror/theme/blackboard.css'
import 'codemirror/addon/hint/show-hint.css'
import 'codemirror/addon/hint/show-hint.js'
import 'codemirror/addon/hint/show-hint'
import 'codemirror/addon/hint/javascript-hint'
import 'codemirror/addon/hint/sql-hint'
import 'codemirror/mode/javascript/javascript'
import 'codemirror/mode/markdown/markdown'
import 'codemirror/mode/sql/sql'
import 'codemirror/mode/php/php'
import 'codemirror/mode/python/python'
import 'codemirror/mode/shell/shell'
import 'codemirror/mode/powershell/powershell'
// export default {
//   data: function () {
//     return {
//       content: '',
//     }
//   },
//   props: {
//     hint: Boolean,
//     code: String,
//     value: String,
//     unseenLines: Array,
//     marker: Function,
//     options: {
//       type: Object,
//       default: function () {
//         return {
//           styleActiveLine: true,
//           lineNumbers: true,
//           mode: 'python',
//           lineWrapping: true,
//           theme: 'blackboard',
//           hint: 'python',
//         }
//       },
//     },
//   },
//   created: function () {
//     this.options = this.options || {}
//     var language = this.options.mode || 'python'
//     var theme = this.options.theme
//     // require active-line.js
//     if (this.options.styleActiveLine)
//       require('codemirror/addon/selection/active-line.js')

//     // require closebrackets.js
//     if (this.options.autoCloseBrackets)
//       require('codemirror/addon/edit/closebrackets.js')

//     // require closetag.js
//     if (this.options.autoCloseTags) require('codemirror/addon/edit/closetag.js')

//     // require styleSelectedText.js
//     if (this.options.styleSelectedText) {
//       require('codemirror/addon/selection/mark-selection.js')
//       require('codemirror/addon/search/searchcursor.js')
//     }

//     // highlightSelectionMatches
//     if (this.options.highlightSelectionMatches) {
//       require('codemirror/addon/scroll/annotatescrollbar.js')
//       require('codemirror/addon/search/matchesonscrollbar.js')
//       require('codemirror/addon/search/searchcursor.js')
//       require('codemirror/addon/search/match-highlighter.js')
//     }

//     // require emacs
//     if (
//       !!this.options.keyMap &&
//       ['emacs', 'sublime', 'vim'].indexOf(this.options.keyMap) > -1
//     ) {
//       require('codemirror/mode/clike/clike.js')
//       require('codemirror/addon/edit/matchbrackets.js')
//       require('codemirror/addon/comment/comment.js')
//       require('codemirror/addon/dialog/dialog.js')
//       require('codemirror/addon/dialog/dialog.css')
//       require('codemirror/addon/search/searchcursor.js')
//       require('codemirror/addon/search/search.js')
//       // console.log(this.options.keyMap)
//       require('codemirror/keymap/' + this.options.keyMap + '.js')
//     }

//     // require fold js
//     if (this.options.foldGutter) {
//       require('codemirror/addon/fold/foldgutter.css')
//       require('codemirror/addon/fold/brace-fold.js')
//       require('codemirror/addon/fold/comment-fold.js')
//       require('codemirror/addon/fold/foldcode.js')
//       require('codemirror/addon/fold/foldgutter.js')
//       require('codemirror/addon/fold/indent-fold.js')
//       require('codemirror/addon/fold/markdown-fold.js')
//       require('codemirror/addon/fold/xml-fold.js')
//     }

//     // require language mode config
//     require('codemirror/mode/' + language + '/' + language + '.js')

//     // require theme config
//     if (!!theme && theme == 'solarized light') theme = 'solarized'
//     if (!!theme && theme != 'default')
//       require('codemirror/theme/' + theme + '.css')
//   },
//   ready: function () {
//     var _this = this
//     this.editor = CodeMirror.fromTextArea(this.$el, this.options)
//     this.editor.setValue(this.code || this.value || this.content)
//     this.editor.on('change', function (cm) {
//       _this.content = cm.getValue()
//       // _this.value = cm.getValue()
//       _this.code = cm.getValue()
//     })
//   },
//   mounted: function () {
//     var _this = this
//     this.editor = CodeMirror.fromTextArea(this.$el, this.options)
//     this.editor.setValue(this.code || this.value || this.content)
//     this.editor.on('change', function (cm) {
//       // _this.editor.showHint();
//       _this.content = cm.getValue()
//       if (_this.$emit) {
//         $emit(_this, 'changed', _this.content)
//         $emit(_this, 'update:value', _this.content)
//       }
//     })
//     this.unseenLineMarkers()
//   },
//   watch: {
//     code: function (newVal) {
//       const editor_value = this.editor.getValue()
//       if (newVal !== editor_value) {
//         let scrollInfo = this.editor.getScrollInfo()
//         this.editor.setValue(newVal)
//         this.content = newVal
//         this.editor.scrollTo(scrollInfo.left, scrollInfo.top)
//       }
//       this.unseenLineMarkers()
//     },
//     value: function (newVal) {
//       const editor_value = this.editor.getValue()
//       if (newVal !== editor_value) {
//         let scrollInfo = this.editor.getScrollInfo()
//         this.editor.setValue(newVal)
//         this.content = newVal
//         this.editor.scrollTo(scrollInfo.left, scrollInfo.top)
//       }
//       this.unseenLineMarkers()
//     },
//   },
//   methods: {
//     unseenLineMarkers: function () {
//       var _this = this
//       if (_this.unseenLines !== undefined && _this.marker !== undefined) {
//         _this.unseenLines.forEach((line) => {
//           var info = _this.editor.lineInfo(line)
//           _this.editor.setGutterMarker(
//             line,
//             'breakpoints',
//             info.gutterMarkers ? null : _this.marker()
//           )
//         })
//       }
//     },
//   },
//   emits: ['changed', 'update:value'],
// }
export default {
  props: {
    hint: Boolean,
    code: String,
    value: String,
    unseenLines: Array,
    marker: Function,
    options: {
      type: Object,
      default: () => ({
        styleActiveLine: true,
        lineNumbers: true,
        mode: 'python',
        lineWrapping: true,
        theme: 'blackboard',
        hint: 'python',
      }),
    },
  },
  setup(props, { emit }) {
    const content = ref('');
    let editor;

    onMounted(() => {
      editor = CodeMirror.fromTextArea(document.querySelector('textarea'), props.options);
      editor.setValue(props.code || props.value || content.value);

      editor.on('change', (cm) => {
        content.value = cm.getValue();
        emit('update:value', content.value);
        emit('changed', content.value);
      });
    });

    onBeforeUnmount(() => {
      if (editor) {
        editor.toTextArea();
      }
    });

    onBeforeUpdate(() => {
      if (props.code && props.code !== editor.getValue()) {
        const scrollInfo = editor.getScrollInfo();
        editor.setValue(props.code);
        editor.scrollTo(scrollInfo.left, scrollInfo.top);
      }
    });

    return {
      content,
    };
  },
};
</script>

<style>
.CodeMirror-code {
  line-height: 1.6em;
  font-family: Menlo, Monaco, Consolas, 'Courier New', monospace;
}
</style>

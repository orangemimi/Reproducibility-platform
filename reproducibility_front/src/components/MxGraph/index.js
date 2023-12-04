import mx from 'mxgraph'
// const mxgraph = mx({
//   "mxBasePath": '/mxgraph'
// })
const mxgraph = mx({
  mxImageBasePath: './src/images',
  mxBasePath: './src',
})
Object.keys(mxgraph).forEach((key) =>{
  window[key] = mxgraph[key]
})
// decode bug https://github.com/jgraph/mxgraph/issues/49
// window.mxGraph = mxgraph.mxGraph
// window.mxGraphModel = mxgraph.mxGraphModel
// window.mxEditor = mxgraph.mxEditor
// window.mxGeometry = mxgraph.mxGeometry
// window.mxDefaultKeyHandler = mxgraph.mxDefaultKeyHandler
// window.mxDefaultPopupMenu = mxgraph.mxDefaultPopupMenu
// window.mxStylesheet = mxgraph.mxStylesheet
// window.mxDefaultToolbar = mxgraph.mxDefaultToolbar
// window.mxConstants = mxgraph.mxConstants
// window.mxUndoManager = mxgraph.mxUndoManager
// window.mxCellOverlay = mxgraph.mxCellOverlay
// window.mxImageExport = mxgraph.mxImageExport
// window.mxXmlCanvas2D = mxgraph.mxXmlCanvas2D
// window.mxConnectionConstraint = mxgraph.mxConnectionConstraint
// window.mxObjectCodec = mxgraph.mxObjectCodec
// window.mxCodec = mxgraph.mxCodec
// window.mxShape = mxgraph.mxShape
// window.mxEdgeHandler = mxgraph.mxEdgeHandler
// window.mxVertexHandler = mxgraph.mxVertexHandler
// window.mxCellEditor = mxgraph.mxCellEditor
// window.mxGraphHandler = mxgraph.mxGraphHandler
// window.mxPerimeter = mxgraph.mxPerimeter
// window.mxCellState = mxgraph.mxCellState
// window.mxHierarchicalLayout = mxgraph.mxHierarchicalLayout
// window.mxFastOrganicLayout = mxgraph.mxFastOrganicLayout
// window.mxCompactTreeLayout = mxgraph.mxCompactTreeLayout
// window.mxCircleLayout = mxgraph.mxCircleLayout
// window.mxMorphing = mxgraph.mxMorphing
// window.mxOrganicLayout = mxgraph.mxOrganicLayout
// window.mxParallelEdgeLayout = mxgraph.mxParallelEdgeLayout
// window.mxXmlRequest = mxgraph.mxXmlRequest
// window.mxImageExport = mxgraph.mxImageExport
// window.mxXmlCanvas2D = mxgraph.mxXmlCanvas2D

export default mxgraph

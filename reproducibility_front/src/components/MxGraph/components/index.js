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

export default mxgraph

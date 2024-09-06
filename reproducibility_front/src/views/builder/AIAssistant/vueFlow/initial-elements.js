const position = { x: 0, y: 0 };
const nodeType = "data";
const edgeType = "animation";

export const initialNodes = [
  {
    id: "1",
    position,
    label: "Start",
    type: "data",
  },
  {
    id: "2",
    position,
    label: "Start",
    type: "data",
  },
  {
    id: "2a",
    position,
    label: "Start",
    type: "process",
  },
  {
    id: "2b",
    position,
    label: "Start",

    type: "process",
  },
  {
    id: "2c",
    position,
    label: "Start",

    type: nodeType,
  },
  {
    id: "2d",
    position,
    label: "Start",
    type: nodeType,
  },
  {
    id: "3",
    position,
    label: "Start",
    type: nodeType,
  },
  {
    id: "4",
    position,
    label: "Start",
    type: nodeType,
  },
  {
    id: "5",
    position,
    label: "Start",
    type: nodeType,
  },
  {
    id: "6",
    position,
    label: "Start",
    type: nodeType,
  },
  {
    id: "7",
    position,
    label: "Start",
    type: "data",
  },
];

export const initialEdges = [
  { id: "e1-2", source: "1", target: "2", type: edgeType },
  { id: "e1-3", source: "1", target: "3", type: edgeType },
  { id: "e2-2a", source: "2", target: "2a", type: edgeType },
  { id: "e2-2b", source: "2", target: "2b", type: edgeType },
  { id: "e2-2c", source: "2", target: "2c", type: edgeType },
  { id: "e2c-2d", source: "2c", target: "2d", type: edgeType },
  { id: "e3-7", source: "3", target: "4", type: edgeType },
  { id: "e4-5", source: "4", target: "5", type: edgeType },
  { id: "e5-6", source: "5", target: "6", type: edgeType },
  { id: "e5-7", source: "5", target: "7", type: edgeType },
];

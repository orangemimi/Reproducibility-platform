/**
 * @param element {HTMLElement}
 * @return {{top: number, left: number}}
 */

export function getOffsetRect(element) {
  let box,top,left,scrollTop,scrollLeft
  if (element) {
    box = element.getBoundingClientRect()
    scrollTop = window.pageYOffset
    scrollLeft = window.pageXOffset

    top = box.top + scrollTop
    left = box.left + scrollLeft
  }
  return { top: Math.round(top), left: Math.round(left) }
}

export default {
  getOffsetRect,
}

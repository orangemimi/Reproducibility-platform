import { ElNotification } from "element-plus";
/**
 * @param {string} behavior
 * @description create /upload
 * @param {string} message
 * @description 成功信息
 */
export function successNotification(behavior, message) {
  ElNotification({
    title: "Success",
    message: `You have ${behavior} the ${message} successfully`,
    type: "success",
  });
}

export function errorNotification(message) {
  ElNotification({
    title: "Error",
    message: message,
    type: "error",
  });
}

export function successNot(message) {
  ElNotification({
    title: "Success",
    message: message,
    type: "success",
  });
}

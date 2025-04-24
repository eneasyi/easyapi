import ACCESS_ENUM from './accessEnum'

/**
 * 检查权限
 * @param loginUser 当前登录用户
 * @param needAccess 需要的权限
 * @returns {boolean} 是否有权限
 */
const checkAccess = (loginUser, needAccess = ACCESS_ENUM.NOT_LOGIN) => {
  // 获取当前用户权限
  const loginUserAccess = loginUser?.userRole || ACCESS_ENUM.NOT_LOGIN
  
  // 不需要权限
  if (needAccess === ACCESS_ENUM.NOT_LOGIN) {
    return true
  }
  
  // 需要用户权限
  if (needAccess === ACCESS_ENUM.USER) {
    if (loginUserAccess === ACCESS_ENUM.NOT_LOGIN || loginUserAccess === ACCESS_ENUM.ADMIN) {
      return false
    }
  }
  
  // 需要管理员权限
  if (needAccess === ACCESS_ENUM.ADMIN) {
    if (loginUserAccess !== ACCESS_ENUM.ADMIN) {
      return false
    }
  }
  
  return true
}

export default checkAccess 
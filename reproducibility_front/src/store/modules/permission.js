const state = {
  role: localStorage.getItem("role"),
};

const mutations = {
  SET_ROLE: (state, role) => {
    state.role = role;
    localStorage.setItem("role", role);
  },
};

const actions = {
  getRole({ commit }, { project, userId }) {
    // debugger;
    return new Promise((resolve) => {
      if (project.creatorId == userId) {
        commit("SET_ROLE", "participant");
      } else if (
        project.memberList != null &&
        project.memberList.some((member) => member.memberId == userId)
      ) {
        project.memberList.filter((item) => item.memberId == userId); //member's userId
        // commit("SET_ROLE", member[0].role);
        commit("SET_ROLE", "participant");
      } else {
        commit("SET_ROLE", "visitor");
      }
      resolve();
    });
  },
};

export default {
  namespaced: true,
  state,
  mutations,
  actions,
};

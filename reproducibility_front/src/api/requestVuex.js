export async function judgeRole(project, userId) {
    return await this.$store.dispatch('permission/getRole', {
        project: project,
        userId: userId,
    })
}

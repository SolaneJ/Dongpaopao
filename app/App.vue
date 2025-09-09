<script>
	import request from '@/utils/request.js'
	import {
		useUserStore
	} from '@/stores/user.js'

	export default {
		onLaunch: function() {
			const user = useUserStore()
			uni.login({
				success: (res) => {
					request({
						url: 'login',
						data: {
							code: res.code
						}
					}).then((res) => {
						user.setUid(res.data.uid)
					})
				}
			})
		}
	}
</script>

<style>
	/*每个页面公共css */
</style>
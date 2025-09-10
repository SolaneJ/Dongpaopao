// stores/user.js
import {
	defineStore
} from 'pinia';

export const useUserStore = defineStore('user', {
	state: () => {
		return {
			info: {
				nickname: null,
				uid: null,
				sex: -1,
				sid: 0,
				phone: null,
				avatar: null,
				registered: false
			}
		};
	},
	actions: {
		setUid(uid) {
			this.info.uid = uid
		},
		setUserInfo(userInfo) {
			this.userInfo = userInfo
		}
	},
	getters: {

	}
});
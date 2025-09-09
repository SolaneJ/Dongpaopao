// stores/user.js
import {
	defineStore
} from 'pinia';

export const useUserStore = defineStore('user', {
	state: () => {
		return {
			nickname: null,
			uid: null,
			sex: -1,
			sid: 0,
			phone: null,
			avatar: null,
			registered: false
		};
	},
	actions: {
		setUid(uid) {
			this.uid = uid
		},
	},
	getters: {

	}
});
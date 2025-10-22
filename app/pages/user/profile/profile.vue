<template>
	<view class="page-container">
		<!-- 用户信息编辑区域 -->
		<view class="edit-container">
			<view class="edit-content">
				<view class="avatar-edit">
					<u-avatar class="avatar-image" size="100" :src="userInfo.avatar" mode="aspectFill"/>
					<button class="avatar-edit-text" open-type="chooseAvatar" @chooseavatar="changeAvatar">更换头像</button>
				</view>

				<view class="form-item">
					<text class="form-label">昵称</text>
					<input type="nickname" class="form-input" v-model="userInfo.nickname" placeholder="请输入昵称" />
				</view>

				<view class="form-item">
					<text class="form-label">ID</text>
					<text class="form-value">{{ userInfo.uid }}</text>
				</view>

				<view class="form-item">
					<text class="form-label">性别</text>
					<picker class="form-picker" mode="selector" :range="genderOptions" :value="genderIndex"
						@change="genderChange">
						<view>{{ genderOptions[genderIndex] }}</view>
					</picker>
				</view>

				<view class="form-item">
					<text class="form-label">学校</text>
					<picker :range="schoolList" :range-key="'sname'" :index="schoolIndex">
						<view>{{ schoolList[schoolIndex].sname }}</view>
					</picker>
					<!-- <input class="form-input" v-model="userInfo.sid" placeholder="请选择学校" /> -->
				</view>

				<view class="form-item">
					<text class="form-label">手机号</text>
					<input class="form-input" v-model="userInfo.phone" placeholder="请授权手机号" disabled />
					<button open-type="getPhoneNumber" @getphonenumber="getPhoneNumber">授权手机号</button>
				</view>

				<!-- 保存 -->
				<button type="primary" class="save-button" @click="saveInfo">保存修改</button>
			</view>
		</view>
	</view>
</template>
<script lang="ts" setup>
	import { reactive, ref } from 'vue';
	import { useUserStore } from '@/stores/user';
	import { onLoad } from '@dcloudio/uni-app'
	import request from '@/utils/request';

	onLoad(() => {
		// 加载学校列表
		request({
			url: "/school/list"
		}).then((res) => {
			schoolList.value = res.data
		})
	})

	const userStore = useUserStore()

	const userInfo = userStore.info

	// 性别picker
	const genderOptions = ref(['男', '女', '保密'])
	const genderIndex = ref(0);

	const genderChange = (e : any) => {
		genderIndex.value = e.detail.value;
	};

	// 学校picker
	const schoolList = ref([{}])
	const schoolIndex = ref(0)

	// 更新头像的回调函数
	const changeAvatar = (res) => {

		const tempAvatarUrl = res.detail.avatarUrl

		uni.uploadFile({
			url: "http://localhost:8181/api/user/avatar?uid=" + userInfo.uid,
			filePath: tempAvatarUrl,
			name: "file",
			success(res) {
				uni.showToast({
					title: "上传头像成功!",
					icon: 'none',
				})
				// 
				userInfo.avatar = JSON.parse(res.data).data + "?t=" + Date.now()
			}
		})
	};

	const getPhoneNumber = (res) => {
		const code = res.detail.code
		request({
			url: "/auth/getPhoneNumber?code=" + code + "&uid=" + userInfo.uid,
		}).then((res) => {
			userInfo.phone = res.data
		})
	}

	const saveInfo = () => {
		request({
			url: "/user/update",
			data: {
				user: userInfo
			},
			method: "PUT"
		})
		uni.showToast({
			title: '保存成功',
			icon: 'success'
		});
		setTimeout(() => {
			uni.navigateBack();
		}, 1500);
	};
</script>
<style>
	page {
		height: 100%;
		background-color: #f5f5f5;
	}

	.page-container {
		height: 100%;
	}

	.edit-container {
		background-color: #fff;
		min-height: 100%;
	}


	.edit-content {
		padding: 0 30rpx;
	}

	.avatar-edit {
		display: flex;
		flex-direction: column;
		align-items: center;
		padding: 40rpx 0;
	}

	.avatar-image {
		width: 160rpx;
		height: 160rpx;
		border-radius: 50%;
	}

	.avatar-edit-text {
		font-size: 28rpx;
		color: #1a73e8;
		margin-top: 20rpx;
	}

	.form-item {
		display: flex;
		align-items: center;
		padding: 30rpx 0;
		border-bottom: 1rpx solid #f0f0f0;
	}

	.form-label {
		width: 150rpx;
		font-size: 28rpx;
		color: #666;
	}

	.form-input {
		flex: 1;
		font-size: 28rpx;
		color: #333;
	}

	.form-value {
		flex: 1;
		font-size: 28rpx;
		color: #999;
	}

	.form-picker {
		flex: 1;
		font-size: 28rpx;
		color: #333;
	}

	.save-button {
		margin-top: 5%;
	}
</style>
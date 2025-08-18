<template>
	<view class="page-container">
		<!-- 用户信息编辑区域 -->
		<view class="edit-container">
			<view class="edit-content">
				<view class="avatar-edit">
					<image class="avatar-image" :src="userInfo.avatar" mode="aspectFill"></image>
					<text class="avatar-edit-text" @click="changeAvatar">更换头像</text>
				</view>

				<view class="form-item">
					<text class="form-label">昵称</text>
					<input class="form-input" v-model="userInfo.name" placeholder="请输入昵称" />
				</view>

				<view class="form-item">
					<text class="form-label">ID</text>
					<text class="form-value">{{ userInfo.id }}</text>
				</view>

				<view class="form-item">
					<text class="form-label">性别</text>
					<picker class="form-picker" mode="selector" :range="genderOptions" :value="genderIndex"
						@change="genderChange">
						<text>{{ genderOptions[genderIndex] }}</text>
					</picker>
				</view>

				<view class="form-item">
					<text class="form-label">学校</text>
					<input class="form-input" v-model="userInfo.school" placeholder="请输入学校名称" />
				</view>

				<view class="form-item">
					<text class="form-label">手机号</text>
					<input class="form-input" v-model="userInfo.phone" placeholder="请输入手机号" type="number" />
				</view>

				<!-- 保存 -->
				<button type="primary" class="save-button" @click="saveInfo">保存修改</button>
			</view>
		</view>
	</view>
</template>
<script lang="ts" setup>
	import { ref } from 'vue';
	// 用户信息
	const userInfo = ref({
		name: '校园跑腿达人',
		id: 'ST20230001',
		avatar: 'https://ai-public.mastergo.com/ai/img_res/8243a04aa54f4e3cda41894c1e6cd0bd.jpg',
		school: '清华大学',
		phone: '13800138000'
	});

	const genderOptions = ['男', '女', '保密'];
	const genderIndex = ref(0);

	const genderChange = (e : any) => {
		genderIndex.value = e.detail.value;
	};

	const changeAvatar = () => {
		uni.chooseImage({
			count: 1,
			success: (res) => {
				userInfo.value.avatar = res.tempFilePaths[0];
			}
		});
	};
	const goBack = () => {
		uni.navigateBack({
			delta: 1 // 返回层级，1 表示返回上一级
		});
	};

	// const goBack = () => {
	// 	uni.navigateBack();
	// };

	const saveInfo = () => {
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
	
	.save-button{
		margin-top: 5%;
	}
</style>
<template>
	<view class="page-container">
		<!-- 我的地址页面 -->
		<view class="address-container">
			<scroll-view class="address-content" scroll-y>
				<u-button @click="addAddress" type="primary">添加新地址</u-button>
				<view class="address-list">
					<view class="address-item" v-for="(item, index) in addressList" :key="index">
						<view class="address-info">
							<text class="address-name">{{ item.sjr }}</text>
							<text class="address-phone">{{ item.phone }}</text>
							<u-tag text="默认" v-if="item.isDefault" plain/>
						</view>
						<text class="address-detail">{{ item.detail }}</text>
						<view class="address-actions">
							<uni-icons type="compose" size="20" color="#666" @click="editAddress(index)"></uni-icons>
							<uni-icons type="trash" size="20" color="#ff4d4f" @click="deleteAddress(index)"></uni-icons>
						</view>
					</view>
				</view>
			</scroll-view>
		</view>
	</view>

	<u-popup :show="showPopup" mode="bottom" round="20" @close="closePopup" safeAreaInsetBottom>
		<u-form labelWidth="auto" labelPosition="top">
			<u-form-item label="收货人" prop="userInfo.name" borderBottom>
				<u-input v-model="formData.sjr" border="none"></u-input>
			</u-form-item>
			<u-form-item label="手机号" prop="userInfo.name" borderBottom ref="item1">
				<u-input v-model="formData.phone" border="none"></u-input>
			</u-form-item>
			<u-form-item label="详细地址" prop="userInfo.name" borderBottom ref="item1">
				<u-input v-model="formData.detail" border="none"></u-input>
			</u-form-item>
			<u-form-item label="设为默认地址" labelPosition="left">
				<template #right><u-switch v-model="formData.isDefault" /></template>
			</u-form-item>
		</u-form>
		<u-button type="primary" @click="saveAddress(isEdit)" :loading="loading">
			{{ isEdit ? '保存修改' : '添加地址' }}
		</u-button>
	</u-popup>
</template>

<script lang="ts" setup>
	import { ref, reactive } from 'vue';
	import { onLoad } from '@dcloudio/uni-app';
	import request from '@/utils/request';
	import { useUserStore } from '@/stores/user';

	const userInfo = useUserStore().info

	// 弹出层状态
	const showPopup = ref(false);
	const isEdit = ref(false);
	const editingIndex = ref(-1);
	const loading = ref(false)

	// 表单数据
	const formData = reactive({
		aid: '',
		sjr: '',
		phone: '',
		detail: '',
		isDefault: false,
		uid: userInfo.uid
	});

	onLoad(() => {
		fetchAddress()
	})

	const fetchAddress = () => {
		request({
			url: "/address/list?uid=" + userInfo.uid,
		}).then((res) => {
			addressList.value = res.data
		})
	}

	// 地址列表
	const addressList = ref([
		{
			sjr: '张三',
			phone: '13800138000',
			detail: '清华大学紫荆公寓1号楼502室',
			isDefault: true
		}
	]);

	const goBack = () => {
		uni.navigateBack();
	};

	// 地址操作
	const addAddress = () => {
		isEdit.value = false;
		editingIndex.value = -1;
		resetForm();
		showPopup.value = true;
	};

	const editAddress = (index : number) => {
		isEdit.value = true;
		editingIndex.value = index;
		// 填充表单数据
		const address = addressList.value[index];
		formData.aid = address.aid;
		formData.sjr = address.sjr;
		formData.phone = address.phone;
		formData.detail = address.detail;
		formData.isDefault = address.isDefault || false;
		showPopup.value = true;
	};

	const deleteAddress = (index : number) => {
		uni.showModal({
			title: '提示',
			content: '确定要删除该地址吗？',
			success: async (res) => {
				if (res.confirm) {
					/* 删除操作 */
					await request({
						url: "/address/delete",
						data: {
							aid: addressList.value[index].aid
						},
						method: "DELETE"
					})
					addressList.value.splice(index, 1);
					uni.showToast({
						title: '删除成功',
						icon: 'success'
					});
				}
			}
		});
	};

	// 确认表单
	const saveAddress = async (isEdit) => {
		if (isEdit) {
			// 调用更新地址接
			loading.value = true
			await request({
				url: "/address/update",
				method: "PUT",
				data: formData
			}).then((res) => {
				uni.showToast({
					title: '更新成功!',
					icon: 'success'
				})
			})
		} else {
			// 调用新增接口
			loading.value = true
			await request({
				url: "/address/add",
				method: "POST",
				data: formData
			}).then((res) => {
				uni.showToast({
					title: res.msg,
					icon: res.errCode === 0 ? 'success' : 'error'
				})
			})
		}
		closePopup()
		resetForm()
		fetchAddress()
	}

	// 关闭弹出层
	const closePopup = () => {
		loading.value = false
		showPopup.value = false;
		resetForm();
	};

	// 重置表单
	const resetForm = () => {
		formData.sjr = '';
		formData.phone = '';
		formData.detail = '';
		formData.isDefault = false;
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

	.address-content {
		height: 100%;
		/* 使用100%高度，让内容自然撑开 */
		padding: 0 30rpx;
		box-sizing: border-box;
		/* 确保内边距包含在高度内 */
	}

	/* 地址列表样式 */
	.address-list {
		padding: 20rpx 0;
	}

	.address-item {
		padding: 30rpx 0;
		border-bottom: 1rpx solid #f0f0f0;
	}

	.address-info {
		display: flex;
		align-items: center;
		margin-bottom: 10rpx;
	}

	.address-name {
		font-size: 28rpx;
		color: #333;
		font-weight: bold;
		margin-right: 20rpx;
	}

	.address-phone {
		font-size: 28rpx;
		color: #666;
		margin-right: 20rpx;
	}

	.address-detail {
		font-size: 26rpx;
		color: #666;
		line-height: 1.5;
		display: block;
		margin-bottom: 20rpx;
	}

	.address-actions {
		display: flex;
		justify-content: flex-end;
		gap: 30rpx;
	}
</style>
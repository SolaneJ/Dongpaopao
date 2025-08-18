<template>
	<view class="page-container">
		<!-- 我的地址页面 -->
		<view class="address-container">
			<scroll-view class="address-content" scroll-y>
				<view class="address-list">
					<view class="address-item" v-for="(item, index) in addressList" :key="index">
						<view class="address-info">
							<text class="address-name">{{ item.name }}</text>
							<text class="address-phone">{{ item.phone }}</text>
							<text class="address-tag" v-if="item.isDefault">默认</text>
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
</template>
<script lang="ts" setup>
	import { ref } from 'vue';

	// 地址列表
	const addressList = ref([
		{
			name: '张三',
			phone: '13800138000',
			detail: '清华大学紫荆公寓1号楼502室',
			isDefault: true
		},
		{
			name: '李四',
			phone: '13900139000',
			detail: '清华大学西区食堂快递柜',
			isDefault: false
		},
		{
			name: '王五',
			phone: '13700137000',
			detail: '清华大学图书馆前台',
			isDefault: false
		}
	]);

	const goBack = () => {
		uni.navigateBack();
	};

	// 地址操作
	const addAddress = () => {
		uni.navigateTo({
			url: '/pages/address/edit'
		});
	};

	const editAddress = (index : number) => {
		uni.navigateTo({
			url: `/pages/address/edit?id=${index}`
		});
	};

	const deleteAddress = (index : number) => {
		uni.showModal({
			title: '提示',
			content: '确定要删除该地址吗？',
			success: (res) => {
				if (res.confirm) {
					addressList.value.splice(index, 1);
					uni.showToast({
						title: '删除成功',
						icon: 'success'
					});
				}
			}
		});
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

	.address-tag {
		font-size: 24rpx;
		color: #1a73e8;
		background-color: #e6f7ff;
		padding: 4rpx 12rpx;
		border-radius: 4px;
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
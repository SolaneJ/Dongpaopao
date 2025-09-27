<template>
	<view class="card">
		<u-title>配送信息</u-title>
		<!-- 配送地址 -->
		<view class="input-group">
			<text class="input-label">配送地址</text>
			<view class="address-selector" @click="showPopup = !showPopup">
				<view v-if="selectedAddr" class="selected-address">
					<view class="selected-header">
						<text class="selected-name">{{ selectedAddrInfo.sjr }}</text>
						<text class="selected-phone">{{ selectedAddrInfo.phone }}</text>
						<text v-if="selectedAddrInfo.isDefault" class="default-tag">默认</text>
					</view>
					<view class="selected-detail">{{ selectedAddrInfo.detail }}</view>
				</view>
				<text v-else class="placeholder">请选择配送地址</text>
				<uni-icons type="arrowright" size="16" color="#999" />
			</view>
		</view>
		<!-- 期望送达时间 -->
		<view class="input-group">
			<text class="input-label">期望送达时间</text>
			<picker mode="time" @change="handleTimeChange">
				<view class="picker">
					<text>{{ selectedTime || '请选择期望送达时间' }}</text>
					<uni-icons type="arrowdown" size="16" color="#999" />
				</view>
			</picker>
		</view>

		<u-popup :show="showPopup" safeAreaInsetBottom safeAreaInsetTop :closeable="true" @close="showPopup=false">
			<view class="popup-content">
				<view class="popup-title">选择配送地址</view>
				<view class="address-list">
					<view v-for="(addr, index) in addrs" :key="addr.aid" class="address-card"
						:class="{'selected': selectedAddrId === addr.aid}" @click="selectAddress(addr)">
						<view class="address-header">
							<view class="address-info">
								<text class="name">{{ addr.sjr }}</text>
								<text class="phone">{{ addr.phone }}</text>
							</view>
							<view v-if="selectedAddrId === addr.aid" class="selected-icon">✓</view>
						</view>
						<view class="address-detail">{{ addr.detail }}</view>
					</view>
				</view>
			</view>
		</u-popup>
	</view>
</template>

<script setup>
	import {
		ref
	} from 'vue';

	import request from '@/utils/request.js'

	import {
		onLoad
	} from '@dcloudio/uni-app'

	import {
		useUserStore
	} from '@/stores/user.js'

	const user = useUserStore()

	const selectedAddr = ref('');
	const selectedAddrId = ref('');
	const selectedAddrInfo = ref({});
	const selectedTime = ref('');

	const showPopup = ref(false)

	const addrs = ref([])

	const loadAddr = () => {
		request({
			url: "/address/list?uid=" + user.info.uid,
		}).then((res) => {
			addrs.value = res.data
			// 如果有默认地址，自动选中
			const defaultAddr = res.data.find(addr => addr.isDefault)
			if (defaultAddr) {
				selectAddress(defaultAddr)
			}
		})
	}

	const selectAddress = (addr) => {
		selectedAddrId.value = addr.aid
		selectedAddr.value = `${addr.sjr} ${addr.phone} ${addr.detail}`
		selectedAddrInfo.value = addr
		showPopup.value = false
	}

	onLoad(() => {
		loadAddr();
	})

	const handleTimeChange = (e) => {
		selectedTime.value = e.detail.value;
	};
</script>

<style scoped>
	.card {
		background-color: #fff;
		border-radius: 12rpx;
		padding: 30rpx;
		margin-bottom: 20rpx;
		box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
	}

	.input-group {
		margin-bottom: 40rpx;
	}

	.input-label {
		display: block;
		font-size: 14px;
		color: #666;
		margin-bottom: 15rpx;
	}

	.picker {
		display: flex;
		align-items: center;
		justify-content: space-between;
		font-size: 16px;
		color: #333;
		height: 50rpx;
		line-height: 50rpx;
		border-bottom: 1px solid #eee;
		padding-bottom: 15rpx;
	}

	.address-selector {
		display: flex;
		align-items: center;
		justify-content: space-between;
		font-size: 16px;
		color: #333;
		min-height: 50rpx;
		line-height: 50rpx;
		border-bottom: 1px solid #eee;
		padding-bottom: 15rpx;
	}

	.placeholder {
		color: #999;
	}

	.selected-address {
		flex: 1;
	}

	.selected-header {
		display: flex;
		align-items: center;
		flex-wrap: wrap;
		gap: 20rpx;
		margin-bottom: 8rpx;
	}

	.selected-name {
		font-size: 16px;
		font-weight: bold;
		color: #333;
	}

	.selected-phone {
		font-size: 15px;
		color: #666;
	}

	.selected-detail {
		font-size: 14px;
		color: #666;
		line-height: 1.4;
	}

	.popup-content {
		padding: 30rpx;
		background-color: #fff;
		border-radius: 20rpx 20rpx 0 0;
		min-height: 60vh;
	}

	.popup-title {
		font-size: 18px;
		font-weight: bold;
		text-align: center;
		margin-bottom: 30rpx;
		padding-bottom: 20rpx;
		border-bottom: 1px solid #eee;
	}

	.address-list {
		display: flex;
		flex-direction: column;
		gap: 20rpx;
	}

	.address-card {
		background: #f8f9fa;
		border-radius: 12rpx;
		padding: 30rpx;
		border: 2rpx solid #e9ecef;
		position: relative;
	}

	.address-card.selected {
		border-color: #007aff;
		background: #f0f8ff;
	}

	.address-header {
		display: flex;
		justify-content: space-between;
		align-items: flex-start;
		margin-bottom: 15rpx;
	}

	.address-info {
		display: flex;
		align-items: center;
		flex-wrap: wrap;
		gap: 20rpx;
		flex: 1;
	}

	.name {
		font-size: 16px;
		font-weight: bold;
		color: #333;
	}

	.phone {
		font-size: 15px;
		color: #666;
	}

	.default-tag {
		background: #ff6b6b;
		color: white;
		font-size: 12px;
		padding: 4rpx 12rpx;
		border-radius: 20rpx;
	}

	.address-detail {
		font-size: 14px;
		color: #666;
		line-height: 1.5;
	}

	.selected-icon {
		color: #007aff;
		font-size: 18px;
		font-weight: bold;
	}
</style>
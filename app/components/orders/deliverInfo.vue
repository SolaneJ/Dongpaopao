<!-- deliverInfo.vue -->
<template>
	<view class="card">
		<u-title>配送信息</u-title>
		<!-- 配送地址 -->
		<view class="input-group">
			<text class="input-label">配送地址</text>
			<view class="address-selector" @click="showPopup = !showPopup">
				<view v-if="selectedAddrInfo.sjr" class="selected-address">
					<view class="selected-header">
						<text class="selected-name">{{ selectedAddrInfo.sjr }}</text>
						<text class="selected-phone">{{ selectedAddrInfo.phone }}</text>
						<u-tag v-if="selectedAddrInfo.isDefault" text="默认" plain/>
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
			<view class="datetime-picker" @click="showDateTimePicker = true">
				<text>{{ selectedDateTime || '请选择期望送达时间' }}</text>
				<uni-icons type="arrowdown" size="16" color="#999" />
			</view>
		</view>

		<!-- 配送地址选择 -->
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
		
		<!-- 日期时间选择器 -->
		<u-datetime-picker
			:show="showDateTimePicker"
			v-model="dateTimeValue"
			mode="datetime"
			:minDate="minDate"
			:maxDate="maxDate"
			@confirm="handleDateTimeConfirm"
			@cancel="showDateTimePicker = false"
		></u-datetime-picker>
	</view>
</template>

<script setup>
	import {
		ref,
		reactive,
		defineExpose
	} from 'vue';

	import request from '@/utils/request.js'

	import {
		onLoad
	} from '@dcloudio/uni-app'

	import {
		useUserStore
	} from '@/stores/user.js'

	const user = useUserStore()

	const selectedAddrId = ref('');
	const selectedAddrInfo = reactive({
		sjr: '',
		phone: '',
		detail: '',
		isDefault: false
	});
	const selectedDateTime = ref('');
	
	// 日期时间选择相关
	const showDateTimePicker = ref(false);
	const dateTimeValue = ref(0);
	const minDate = ref(0);
	const maxDate = ref(0);

	const showPopup = ref(false)

	const addrs = ref([])

	// 初始化日期时间选择器范围
	const initDateTimeRange = () => {
		const now = new Date();
		minDate.value = now.getTime(); // 当前时间
		
		// 最大可选时间为当前时间加30天
		const maxDateObj = new Date();
		maxDateObj.setDate(now.getDate() + 30);
		maxDate.value = maxDateObj.getTime();
		
		// 设置默认值为当前时间加1小时
		const defaultTime = new Date();
		defaultTime.setHours(now.getHours() + 1);
		dateTimeValue.value = defaultTime.getTime();
	}

	// 加载地址列表
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

	// 选择地址
	const selectAddress = (addr) => {
		selectedAddrId.value = addr.aid
		Object.assign(selectedAddrInfo, {
			sjr: addr.sjr,
			phone: addr.phone,
			detail: addr.detail,
			isDefault: addr.isDefault,
			aid: addr.aid
		})
		showPopup.value = false
	}

	onLoad(() => {
		loadAddr();
		initDateTimeRange();
	})

	// 处理日期时间选择
	const handleDateTimeConfirm = (e) => {
		const date = new Date(e.value);
		
		// 格式化显示：YYYY-MM-DD HH:mm
		const year = date.getFullYear();
		const month = String(date.getMonth() + 1).padStart(2, '0');
		const day = String(date.getDate()).padStart(2, '0');
		const hours = String(date.getHours()).padStart(2, '0');
		const minutes = String(date.getMinutes()).padStart(2, '0');
		
		selectedDateTime.value = `${year}-${month}-${day} ${hours}:${minutes}`;
		showDateTimePicker.value = false;
	};

	// 表单验证
	const validateForm = () => {
		if (!selectedAddrInfo.sjr) {
			uni.showToast({
				title: '请选择配送地址',
				icon: 'none'
			});
			return false;
		}

		if (!selectedDateTime.value) {
			uni.showToast({
				title: '请选择期望送达时间',
				icon: 'none'
			});
			return false;
		}

		// 验证选择的日期时间是否在当前时间之后
		const selectedTime = new Date(dateTimeValue.value);
		const now = new Date();
		if (selectedTime <= now) {
			uni.showToast({
				title: '送达时间必须晚于当前时间',
				icon: 'none'
			});
			return false;
		}

		return true;
	}

	// 获取表单数据
	const getFormData = () => {
		// 将日期时间转换为后端需要的格式 (LocalDateTime)
		// 格式: YYYY-MM-DDTHH:mm:ss
		const date = new Date(dateTimeValue.value);
		const year = date.getFullYear();
		const month = String(date.getMonth() + 1).padStart(2, '0');
		const day = String(date.getDate()).padStart(2, '0');
		const hours = String(date.getHours()).padStart(2, '0');
		const minutes = String(date.getMinutes()).padStart(2, '0');
		const seconds = String(date.getSeconds()).padStart(2, '0');
		
		const localDateTime = `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`;
		
		return {
			aid: selectedAddrId.value,
			expectTime: localDateTime // 格式: 2023-12-01T14:30:00
		};
	}

	// 暴露方法给父组件
	defineExpose({
		validateForm,
		getFormData
	})
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

	.datetime-picker {
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
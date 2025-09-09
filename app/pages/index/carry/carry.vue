<template>
	<view class="page-container">
		<!-- 地图组件 -->
		<view class="map-container">
			<map class="map" :latitude="location.latitude" :longitude="location.longitude" :markers="markers"
				show-location></map>
		</view>
		<scroll-view scroll-y class="content-container">
			<!-- 搬运信息区域 -->
			<view class="card">
				<view class="section-title">
					<text>搬运信息</text>
				</view>
				<!-- 物品描述 -->
				<view class="input-group">
					<text class="input-label">物品描述</text>
					<textarea v-model="movingInfo.description" placeholder="请描述需要搬运的物品（如：家具、电器等）"
						class="textarea-field" />
				</view>
				<!-- 物品重量 -->
				<view class="input-group">
					<text class="input-label">预估重量</text>
					<picker mode="selector" :range="weightOptions" @change="handleWeightChange">
						<view class="picker">
							{{movingInfo.weight || '请选择物品重量'}}
							<uni-icons type="arrowdown" size="16" color="#999" />
						</view>
					</picker>
				</view>
				<!-- 物品数量 -->
				<view class="input-group">
					<text class="input-label">物品数量</text>
					<view class="counter-wrapper">
						<uni-icons type="minus" size="20" color="#999" @click="decreaseCount" />
						<text class="counter-number">{{movingInfo.count}}</text>
						<uni-icons type="plus" size="20" color="#999" @click="increaseCount" />
					</view>
				</view>
				<!-- 是否需要电梯 -->
				<view class="input-group">
					<text class="input-label">是否有电梯</text>
					<view class="toggle-wrapper">
						<text class="toggle-label">取货地点</text>
						<switch :checked="movingInfo.hasPickupElevator" @change="handlePickupElevatorChange"
							color="#2979ff" />
					</view>
					<view class="toggle-wrapper" style="margin-top: 20rpx;">
						<text class="toggle-label">送货地点</text>
						<switch :checked="movingInfo.hasDeliveryElevator" @change="handleDeliveryElevatorChange"
							color="#2979ff" />
					</view>
				</view>
				<!-- 备注 -->
				<view class="input-group">
					<text class="input-label">备注信息</text>
					<textarea v-model="takeoutInfo.remark" placeholder="可填写餐品相关的特殊要求（选填）" class="textarea-field" />
				</view>
			</view>
			<!-- 配送信息区域 -->
			<view class="card">
				<view class="section-title">
					<text>配送信息</text>
				</view>
				<!-- 收件人姓名 -->
				<view class="input-group">
					<text class="input-label">收件人</text>
					<input v-model="deliveryInfo.name" placeholder="请输入收件人姓名" class="input-field" />
				</view>
				<!-- 手机号码 -->
				<view class="input-group">
					<text class="input-label">手机号码</text>
					<input v-model="deliveryInfo.phone" placeholder="请输入手机号码" class="input-field" type="number" />
				</view>
				<!-- 配送地址 -->
				<view class="input-group">
					<text class="input-label">配送地址</text>
					<view class="address-selector" @click="goToAddressPage">
						<text>{{deliveryInfo.address || '请选择配送地址'}}</text>
						<uni-icons type="arrowright" size="16" color="#999" />
					</view>
				</view>
				<!-- 期望送达时间 -->
				<view class="input-group">
					<text class="input-label">期望送达时间</text>
					<picker mode="time" @change="handleTimeChange">
						<view class="picker">
							{{deliveryInfo.time || '请选择期望送达时间'}}
							<uni-icons type="arrowdown" size="16" color="#999" />
						</view>
					</picker>
				</view>
			</view>
			<!-- 费用信息区域 -->
			<view class="card">
				<view class="section-title">
					<text>费用明细</text>
				</view>
				<view class="fee-item">
					<text>基础配送费</text>
					<text>¥6.00</text>
				</view>
				<view class="fee-item">
					<text>距离附加费</text>
					<text>¥2.00</text>
				</view>
				<view class="fee-item coupon">
					<text>优惠券</text>
					<text class="coupon-text">选择优惠券</text>
				</view>
				<view class="total-fee">
					<text>总计</text>
					<text class="price">{{getTotalPrice}}</text>
				</view>
			</view>
		</scroll-view>
		<!-- 底部操作区 -->
		<view class="footer">
			<view class="price-info">
				<text>合计: </text>
				<text class="total-price">¥7.00</text>
			</view>
			<button class="submit-btn" @click="handleSubmit">立即下单</button>
		</view>
	</view>
</template>
<script lang="ts" setup>
	import { ref, computed } from 'vue';
	
	// 基本信息
	const takeoutInfo = ref({
		remark: ''
	})
	// 地图位置信息
	const location = ref({
		latitude: 39.908692,
		longitude: 116.397477,
	});
	const markers = ref([
		{
			id: 1,
			latitude: 39.908692,
			longitude: 116.397477,
			title: '当前位置',
			iconPath: '/static/location.png',
			width: 32,
			height: 32
		}
	]);
	// 快递信息
	const movingInfo = ref({
		description: '',
		weight: '',
		count: 1,
		hasPickupElevator: false,
		hasDeliveryElevator: false,
		remark: ''
	});

	const weightOptions = [
		'10kg以下',
		'10-30kg',
		'30-50kg',
		'50-100kg',
		'100kg以上'
	];

	const handleWeightChange = (e : any) => {
		movingInfo.value.weight = weightOptions[e.detail.value];
	};

	const increaseCount = () => {
		if (movingInfo.value.count < 10) {
			movingInfo.value.count++;
		}
	};

	const decreaseCount = () => {
		if (movingInfo.value.count > 1) {
			movingInfo.value.count--;
		}
	};

	const handlePickupElevatorChange = (e : any) => {
		movingInfo.value.hasPickupElevator = e.detail.value;
	};

	const handleDeliveryElevatorChange = (e : any) => {
		movingInfo.value.hasDeliveryElevator = e.detail.value;
	};
	const qrCodeImage = ref('');
	const handleUploadQR = () => {
		uni.chooseImage({
			count: 1,
			sizeType: ['original', 'compressed'],
			sourceType: ['album', 'camera'],
			success: (res) => {
				qrCodeImage.value = res.tempFilePaths[0];
			}
		});
	};
	const handleDeleteQR = () => {
		qrCodeImage.value = '';
	};
	// 配送信息
	const deliveryInfo = ref({
		name: '',
		phone: '',
		address: '',
		time: ''
	});
	// 快递公司列表
	const expressCompanies = ref([
		'顺丰速运',
		'中通快递',
		'圆通速递',
		'韵达快递',
		'申通快递',
		'京东物流',
		'邮政快递'
	]);
	// 存放位置列表
	const storageLocations = ref([
		'菜鸟驿站',
		'快递柜',
		'宿舍楼下',
		'教学楼快递点',
		'校门口快递点'
	]);
	// 配送地址列表
	const deliveryAddresses = ref([
		'1号楼101室',
		'2号楼202室',
		'3号楼303室',
		'4号楼404室',
		'5号楼505室'
	]);
	const handleBack = () => {
		uni.navigateBack();
	};
	const showHelp = () => {
		uni.showToast({
			title: '客服帮助',
			icon: 'none'
		});
	};
	const handleScan = () => {
		uni.scanCode({
			success: (res) => {
				expressInfo.value.code = res.result;
			}
		});
	};
	const handleCompanyChange = (e : any) => {
		expressInfo.value.company = expressCompanies.value[e.detail.value];
	};
	const handleLocationChange = (e : any) => {
		expressInfo.value.location = storageLocations.value[e.detail.value];
	};
	const goToAddressPage = () => {
		uni.navigateTo({
			url: '/pages/index/address'
		});
	};
	// 监听地址选择结果
	uni.$on('addressSelected', (address : string) => {
		deliveryInfo.value.address = address;
	});
	const handleTimeChange = (e : any) => {
		deliveryInfo.value.time = e.detail.value;
	};
	const handleSubmit = () => {
		if (!expressInfo.value.code) {
			uni.showToast({
				title: '请输入取件码或快递单号',
				icon: 'none'
			});
			return;
		}
		if (!expressInfo.value.company) {
			uni.showToast({
				title: '请选择快递公司',
				icon: 'none'
			});
			return;
		}
		if (!deliveryInfo.value.name) {
			uni.showToast({
				title: '请输入收件人姓名',
				icon: 'none'
			});
			return;
		}
		if (!deliveryInfo.value.phone) {
			uni.showToast({
				title: '请输入手机号码',
				icon: 'none'
			});
			return;
		}
		uni.showToast({
			title: '下单成功',
			icon: 'success'
		});
	};
</script>
<style>
	page {
		height: 100%;
	}

	.page-container {
		display: flex;
		flex-direction: column;
		height: 100%;
		background-color: #f7f7f7;
	}

	.map-container {
		width: 100%;
		height: 400rpx;
	}

	.map {
		width: 100%;
		height: 100%;
	}

	.nav-bar {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 20rpx 30rpx;
		background-color: #fff;
		border-bottom: 1px solid #eee;
	}

	.nav-title {
		font-size: 18px;
		font-weight: 500;
		color: #333;
	}

	.content-container {
		flex: 1;
		overflow: auto;
		padding: 20rpx;
	}

	.card {
		background-color: #fff;
		border-radius: 12rpx;
		padding: 30rpx;
		margin-bottom: 20rpx;
		box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
	}

	.section-title {
		font-size: 16px;
		font-weight: 500;
		color: #333;
		margin-bottom: 30rpx;
		position: relative;
		padding-left: 20rpx;
	}

	.section-title::before {
		content: '';
		position: absolute;
		left: 0;
		top: 50%;
		transform: translateY(-50%);
		width: 8rpx;
		height: 30rpx;
		background-color: #2979ff;
		border-radius: 4rpx;
	}

	.input-group {
		margin-bottom: 40rpx;
	}

	.counter-wrapper {
		display: flex;
		align-items: center;
		justify-content: flex-start;
		padding: 10rpx 0;
		border-bottom: 1px solid #eee;
	}

	.counter-number {
		margin: 0 30rpx;
		font-size: 16px;
		color: #333;
		min-width: 40rpx;
		text-align: center;
	}

	.toggle-wrapper {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 10rpx 0;
		border-bottom: 1px solid #eee;
	}

	.toggle-label {
		font-size: 14px;
		color: #666;
	}

	.input-label {
		display: block;
		font-size: 14px;
		color: #666;
		margin-bottom: 15rpx;
	}

	.input-wrapper {
		display: flex;
		align-items: center;
		border-bottom: 1px solid #eee;
		padding-bottom: 15rpx;
	}

	.input-field {
		flex: 1;
		font-size: 16px;
		height: 50rpx;
		line-height: 50rpx;
	}

	.textarea-field {
		width: 100%;
		height: 120rpx;
		font-size: 16px;
		border: 1px solid #eee;
		border-radius: 8rpx;
		padding: 15rpx;
		box-sizing: border-box;
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
		height: 50rpx;
		line-height: 50rpx;
		border-bottom: 1px solid #eee;
		padding-bottom: 15rpx;
	}

	.fee-item {
		display: flex;
		justify-content: space-between;
		font-size: 14px;
		color: #666;
		margin-bottom: 20rpx;
	}

	.coupon {
		color: #2979ff;
	}

	.coupon-text {
		font-size: 14px;
	}

	.total-fee {
		display: flex;
		justify-content: space-between;
		font-size: 16px;
		color: #333;
		margin-top: 30rpx;
		padding-top: 20rpx;
		border-top: 1px dashed #eee;
	}

	.price {
		color: #ff4d4f;
		font-weight: 500;
	}

	.footer {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 20rpx 30rpx;
		background-color: #fff;
		border-top: 1px solid #eee;
	}

	.price-info {
		display: flex;
		align-items: center;
		font-size: 14px;
		color: #666;
	}

	.total-price {
		font-size: 18px;
		color: #ff4d4f;
		font-weight: 500;
		margin-left: 10rpx;
	}

	.submit-btn {
		flex: 1;
		margin-left: 20rpx;
		background-color: #2979ff;
		color: #fff;
		border-radius: 50rpx;
		font-size: 16px;
		height: 80rpx;
		line-height: 80rpx;
	}
</style>
<template>
	<view class="page-container">
		<!-- 地图组件 -->
		<view class="map-container">
			<map class="map" :latitude="location.latitude" :longitude="location.longitude" :markers="markers"
				show-location></map>
		</view>
		<scroll-view scroll-y class="content-container">
			<!-- 快递信息区域 -->
			<view class="card">
				<view class="section-title">
					<text>快递信息</text>
				</view>
				<!-- 取件码输入 -->
				<view class="input-group">
					<text class="input-label">取件码/单号</text>
					<view class="input-wrapper">
						<input v-model="expressInfo.code" placeholder="请输入取件码或快递单号" class="input-field" />
						<uni-icons type="scan" size="20" color="#999" @click="handleScan" />
					</view>
				</view>
				<!-- 快递公司选择 -->
				<view class="input-group">
					<text class="input-label">快递公司</text>
					<picker mode="selector" :range="expressCompanies" @change="handleCompanyChange">
						<view class="picker">
							{{expressInfo.company || '请选择快递公司'}}
							<uni-icons type="arrowdown" size="16" color="#999" />
						</view>
					</picker>
				</view>
				<!-- 存放位置 -->
				<view class="input-group">
					<text class="input-label">存放位置</text>
					<picker mode="selector" :range="storageLocations" @change="handleLocationChange">
						<view class="picker">
							{{expressInfo.location || '请选择存放位置'}}
							<uni-icons type="arrowdown" size="16" color="#999" />
						</view>
					</picker>
				</view>
				<!-- 备注 -->
				<view class="input-group">
					<text class="input-label">备注信息</text>
					<textarea v-model="expressInfo.remark" placeholder="可填写特殊要求（选填）" class="textarea-field" />
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
					<text>¥5.00</text>
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
					<text class="price">¥7.00</text>
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
	import { ref } from 'vue';
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
	const expressInfo = ref({
		code: '',
		company: '',
		location: '',
		remark: ''
	});
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
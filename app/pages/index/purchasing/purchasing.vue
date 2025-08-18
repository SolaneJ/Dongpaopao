<template>
	<view class="page-container">
		<scroll-view scroll-y class="content-container">
			<!-- 代购信息区域 -->
			<view class="card">
				<view class="section-title">
					<text>代购信息</text>
				</view>
				<!-- 商品链接 -->
				<view class="input-group">
					<text class="input-label">商品链接</text>
					<textarea v-model="purchaseInfo.productLink" placeholder="请粘贴商品链接" class="textarea-field" />
				</view>
				<!-- 商品描述 -->
				<view class="input-group">
					<text class="input-label">商品描述</text>
					<textarea v-model="purchaseInfo.description" placeholder="请描述商品的具体规格、型号、颜色等信息"
						class="textarea-field" />
				</view>
				<!-- 商品数量 -->
				<view class="input-group">
					<text class="input-label">购买数量</text>
					<view class="counter-wrapper">
						<uni-icons type="minus" size="20" color="#999" @click="decreaseCount" />
						<text class="counter-number">{{purchaseInfo.count}}</text>
						<uni-icons type="plus" size="20" color="#999" @click="increaseCount" />
					</view>
				</view>
				<!-- 商品价格 -->
				<view class="input-group">
					<text class="input-label">商品价格</text>
					<input v-model="purchaseInfo.price" type="digit" placeholder="请输入商品价格" class="input-field" />
				</view>
				<!-- 备注 -->
				<view class="input-group">
					<text class="input-label">备注信息</text>
					<textarea v-model="purchaseInfo.remark" placeholder="可填写商品相关的特殊要求（选填）" class="textarea-field" />
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
					<text>服务费</text>
					<text>¥6.00</text>
				</view>
				<view class="fee-item coupon">
					<text>优惠券</text>
					<text class="coupon-text">选择优惠券</text>
				</view>
				<view class="total-fee">
					<text>总计</text>
					<text class="price">¥{{totalPrice}}</text>
				</view>
			</view>
		</scroll-view>
		<!-- 底部操作区 -->
		<view class="footer">
			<view class="price-info">
				<text>合计: </text>
				<text class="total-price">¥{{totalPrice}}</text>
			</view>
			<button class="submit-btn" @click="handleSubmit">立即下单</button>
		</view>
	</view>
</template>

<script lang="ts" setup>
	import { ref, computed } from 'vue';

	// 代购信息
	const purchaseInfo = ref({
		productLink: '',
		description: '',
		count: 1,
		price: '',
		remark: ''
	});

	// 配送信息
	const deliveryInfo = ref({
		name: '',
		phone: '',
		address: '',
		time: ''
	});

	// 计算总价
	const totalPrice = computed(() => {
		const baseFee = 6; // 基础配送费
		const distanceFee = 2; // 距离附加费
		const productPrice = parseFloat(purchaseInfo.value.price) || 0;

		return (baseFee + distanceFee + productPrice * purchaseInfo.value.count).toFixed(2);
	});

	const increaseCount = () => {
		if (purchaseInfo.value.count < 10) {
			purchaseInfo.value.count++;
		}
	};

	const decreaseCount = () => {
		if (purchaseInfo.value.count > 1) {
			purchaseInfo.value.count--;
		}
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
		if (!purchaseInfo.value.productLink) {
			uni.showToast({
				title: '请输入商品链接',
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

		if (!deliveryInfo.value.address) {
			uni.showToast({
				title: '请选择配送地址',
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

	.input-label {
		display: block;
		font-size: 14px;
		color: #666;
		margin-bottom: 15rpx;
	}

	.input-field {
		flex: 1;
		font-size: 16px;
		height: 50rpx;
		line-height: 50rpx;
		border-bottom: 1px solid #eee;
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
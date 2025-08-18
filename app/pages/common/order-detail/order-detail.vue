<template>
	<view class="container">
		<!-- 订单状态区 -->
		<view class="status-section">
			<view class="status-header">
				<view class="status-title">
					<text class="title">订单详情</text>
				</view>
				<view class="share-action" @click="shareOrder">
					<uni-icons type="share" size="18" color="#666666"></uni-icons>
				</view>
			</view>
			<view class="order-status">
				<view class="status-icon">
					<uni-icons :type="orderData.statusIcon" size="32" :color="orderData.statusColor"></uni-icons>
				</view>
				<view class="status-content">
					<text class="status-text">{{ orderData.statusText }}</text>
					<text class="status-desc">{{ orderData.statusDesc }}</text>
				</view>
			</view>
		</view>

		<!-- 订单详情区 -->
		<scroll-view class="order-details" scroll-y>
			<!-- 加载状态 -->
			<view v-if="loading" class="loading-view">
				<uni-loading-icon size="24" color="#1a73e8"></uni-loading-icon>
				<text class="loading-text">加载中...</text>
			</view>

			<!-- 错误状态 -->
			<view v-if="error" class="error-view">
				<uni-icons type="warn" size="36" color="#ff6b6b"></uni-icons>
				<text class="error-text">{{ errorMsg }}</text>
				<button class="retry-btn" @click="retryLoad">重试</button>
			</view>

			<!-- 订单内容（仅在数据正常时显示） -->
			<view v-if="!loading && !error">
				<view class="address-section">
					<view class="address-icon">
						<uni-icons type="location" size="20" color="#333333"></uni-icons>
					</view>
					<view class="address-content">
						<view class="address-title">
							<text class="location-text">从{{ orderData.from }}到{{ orderData.to }}</text>
							<text class="distance">{{ orderData.distance }}</text>
						</view>
						<text class="address-desc">{{ orderData.from }} → {{ orderData.to }}</text>
					</view>
				</view>

				<view class="order-items">
					<view class="item">
						<image class="item-image" :src="orderData.itemImage" mode="aspectFill"
							@error="handleImageError"></image>
						<view class="item-info">
							<text class="item-name">{{ orderData.itemName }}</text>
							<view class="item-price">
								<text class="price">¥{{ orderData.price.toFixed(2) }}</text>
								<text class="time">{{ formatTime(orderData.completionTime) }}</text>
							</view>
						</view>
					</view>
				</view>

				<view class="order-info">
					<view class="info-item">
						<text class="label">订单编号</text>
						<text class="value">{{ orderData.orderNo }}</text>
					</view>
					<view class="info-item">
						<text class="label">下单时间</text>
						<text class="value">{{ formatTime(orderData.orderTime) }}</text>
					</view>
					<view class="info-item">
						<text class="label">支付方式</text>
						<text class="value">{{ orderData.paymentMethod }}</text>
					</view>
				</view>
			</view>
		</scroll-view>

		<!-- 底部操作按钮 -->
		<view class="action-bar" v-if="showActionButtons && !loading && !error">
			<button class="action-btn" v-if="orderData.status === '待接单'" @click="acceptOrder">接单</button>
			<button class="action-btn" v-if="orderData.status === '进行中'" @click="completeOrder">完成订单</button>
			<button class="action-btn cancel-btn" v-if="orderData.status === '待接单' || orderData.status === '进行中'"
				@click="cancelOrder">取消订单</button>
		</view>
	</view>
</template>

<script lang="ts" setup>
	import { ref, getCurrentInstance } from 'vue';
	import { onLoad } from '@dcloudio/uni-app';

	// 定义订单数据类型接口
	interface OrderData {
		id : number;
		orderNo : string;
		status : string;
		statusIcon : string;
		statusColor : string;
		statusText : string;
		statusDesc : string;
		from : string;
		to : string;
		distance : string;
		itemImage : string;
		itemName : string;
		price : number;
		completionTime : string;
		orderTime : string;
		paymentMethod : string;
	}

	// 订单数据
	const orderData = ref<OrderData>({
		id: 0,
		orderNo: '',
		status: '',
		statusIcon: '',
		statusColor: '',
		statusText: '',
		statusDesc: '',
		from: '',
		to: '',
		distance: '',
		itemImage: '',
		itemName: '',
		price: 0,
		completionTime: '',
		orderTime: '',
		paymentMethod: ''
	});

	// 状态管理
	const showActionButtons = ref(false);
	const loading = ref(true); // 加载状态
	const error = ref(false); // 错误��态
	const errorMsg = ref(''); // 错误信息

	// 格式化时间显示
	const formatTime = (timeStr : string) => {
		if (!timeStr) return '暂无数据';
		// 简单处理：如果是日期字符串，格式化显示
		const date = new Date(timeStr);
		if (isNaN(date.getTime())) return timeStr; // 非日期格式直接返回
		return date.toLocaleString('zh-CN', {
			month: '2-digit',
			day: '2-digit',
			hour: '2-digit',
			minute: '2-digit'
		});
	};

	// 处理图片加载失败
	const handleImageError = () => {
		orderData.value.itemImage = '/static/default.png'; // 替换为默认图片
	};

	// 根据状态设置图标和颜色
	const setStatusInfo = (status : string) => {
		switch (status) {
			case '待接单':
				orderData.value.statusIcon = 'circle-filled';
				orderData.value.statusColor = '#FF9800';
				orderData.value.statusText = '待接单';
				orderData.value.statusDesc = '等待骑手接单';
				showActionButtons.value = true;
				break;
			case '进行中':
				orderData.value.statusIcon = 'auth';
				orderData.value.statusColor = '#2196F3';
				orderData.value.statusText = '进行中';
				orderData.value.statusDesc = '骑手正在为您服务';
				showActionButtons.value = true;
				break;
			case '已完成':
				orderData.value.statusIcon = 'checkbox-filled';
				orderData.value.statusColor = '#4CAF50';
				orderData.value.statusText = '订单已完成';
				orderData.value.statusDesc = `订单已于${formatTime(orderData.value.completionTime)}送达`;
				showActionButtons.value = false;
				break;
			case '已取消':
				orderData.value.statusIcon = 'closeempty';
				orderData.value.statusColor = '#9E9E9E';
				orderData.value.statusText = '订单已取消';
				orderData.value.statusDesc = '订单已被取消';
				showActionButtons.value = false;
				break;
			default:
				orderData.value.statusIcon = 'info';
				orderData.value.statusColor = '#333';
				orderData.value.statusText = status;
				orderData.value.statusDesc = '';
				showActionButtons.value = false;
		}
	};

	// 重新加载数据
	const retryLoad = () => {
		loading.value = true;
		error.value = false;
		errorMsg.value = '';
		// 重新获取数据（复用onLoad中的逻辑）
		loadOrderData();
	};

	// 加载订单数据的核心逻辑
	const loadOrderData = () => {
		const eventChannel = getOpenerEventChannel();

		// 监听数据接收
		const dataHandler = (data : any) => {
			if (!data || !data.orderDetail) {
				error.value = true;
				errorMsg.value = '未获取到有效订单数据';
				loading.value = false;
				return;
			}

			const orderDetail = data.orderDetail;

			// 填充订单数据
			orderData.value = {
				id: orderDetail.id || 0,
				orderNo: orderDetail.orderNo || '',
				status: orderDetail.status || '',
				statusIcon: '',
				statusColor: '',
				statusText: '',
				statusDesc: '',
				from: orderDetail.from || '',
				to: orderDetail.to || '',
				distance: orderDetail.distance || '',
				itemImage: orderDetail.image || '/static/default.png',
				itemName: orderDetail.title || '未知商品',
				price: orderDetail.price || 0,
				completionTime: orderDetail.completionTime || '',
				orderTime: orderDetail.time || '',
				paymentMethod: orderDetail.paymentMethod || '未知'
			};

			// 设置状态相关信息
			setStatusInfo(orderDetail.status || '');
			loading.value = false;
		};

		// 监听错误
		const errorHandler = (err : any) => {
			console.error('事件通道错误:', err);
			error.value = true;
			errorMsg.value = '获取订单失败，请重试';
			loading.value = false;
		};

		// 监听数据
		eventChannel.on('acceptOrderDetail', dataHandler);
		eventChannel.on('error', errorHandler);

		// 防止内存泄漏：页面卸载时移除监听
		onUnload(() => {
			eventChannel.off('acceptOrderDetail', dataHandler);
			eventChannel.off('error', errorHandler);
		});
	};

	// 分享订单
	const shareOrder = () => {
		// 检查是否支持分享
		if (!uni.canIUse('share')) {
			showToast({
				title: '当前环境不支持分享',
				icon: 'none'
			});
			return;
		}

		uni.share({
			provider: 'weixin',
			scene: 'WXSceneSession',
			type: 0,
			title: '校园帮订单分享',
			summary: `我有一个${orderData.value.itemName}的订单需要帮助`,
			success: () => {
				showToast({
					title: '分享成功',
					icon: 'success'
				});
			},
			fail: (err) => {
				console.log('分享失败:', err);
				showToast({
					title: '分享���败',
					icon: 'none'
				});
			}
		});
	};

	// 接单
	const acceptOrder = () => {
		uni.showModal({
			title: '提示',
			content: '确定要接单吗？',
			success: (res) => {
				if (res.confirm) {
					// 模拟接口请求延迟
					loading.value = true;
					setTimeout(() => {
						orderData.value.status = '进行中';
						setStatusInfo('进行中');
						showToast({
							title: '接单成功',
							icon: 'success'
						});
						loading.value = false;
					}, 500);
				}
			}
		});
	};

	// 完成订单
	const completeOrder = () => {
		uni.showModal({
			title: '提示',
			content: '确定订单已完成吗？',
			success: (res) => {
				if (res.confirm) {
					loading.value = true;
					setTimeout(() => {
						orderData.value.status = '已完成';
						orderData.value.completionTime = new Date().toISOString();
						setStatusInfo('已完成');
						showToast({
							title: '订单已完成',
							icon: 'success'
						});
						loading.value = false;
					}, 500);
				}
			}
		});
	};

	// 取消订单
	const cancelOrder = () => {
		uni.showModal({
			title: '提示',
			content: '确定要取消订单吗？',
			success: (res) => {
				if (res.confirm) {
					loading.value = true;
					setTimeout(() => {
						orderData.value.status = '已取消';
						setStatusInfo('已取消');
						showToast({
							title: '订单已取消',
							icon: 'success'
						});
						loading.value = false;
					}, 500);
				}
			}
		});
	};

	// 页面加载时获取订单数据
	onLoad((options) => {
		if (!options || !options.id) {
			error.value = true;
			errorMsg.value = '订单参数错误';
			loading.value = false;
			setTimeout(() => {
				uni.navigateBack();
			}, 1500);
			return;
		}
		// 直接从 options 读取所有字段
		orderData.value = {
			id: Number(options.id) || 0,
			orderNo: options.orderNo || '',
			status: options.status || '',
			statusIcon: '',
			statusColor: '',
			statusText: '',
			statusDesc: '',
			from: options.from || '',
			to: options.to || '',
			distance: options.distance || '',
			itemImage: options.image || '/static/default.png',
			itemName: options.title || '未知商品',
			price: options.price ? Number(options.price) : 0,
			completionTime: options.completionTime || '',
			orderTime: options.time || '',
			paymentMethod: options.paymentMethod || '未知'
		};
		setStatusInfo(orderData.value.status);
		loading.value = false;
	});

	// 页面卸载时清理
	const onUnload = () => {
		// 清理逻辑（在loadOrderData中已绑定）
	};

	// 使用uni.showToast替代直接导入showToast
	const showToast = (options : any) => {
		if (typeof options === 'string') {
			uni.showToast({ title: options, icon: 'none' });
		} else {
			uni.showToast(options);
		}
	};
</script>
<style>
	page {
		height: 100%;
	}

	.page {
		height: 100%;
		background-color: #f5f5f5;
	}

	.container {
		height: 100%;
		display: flex;
		flex-direction: column;
	}

	.status-section {
		background-color: #ffffff;
		padding: 30rpx;
		margin-bottom: 20rpx;
	}

	.status-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 40rpx;
	}

	.status-title {
		display: flex;
		align-items: center;
	}

	.title {
		font-size: 18px;
		font-weight: bold;
		color: #333333;
	}

	.share-action {
		padding: 10rpx;
	}

	.order-status {
		display: flex;
		align-items: flex-start;
		padding: 20rpx 0;
	}

	.status-icon {
		margin-right: 20rpx;
		padding-top: 6rpx;
	}

	.status-content {
		flex: 1;
	}

	.status-text {
		font-size: 16px;
		color: #07c160;
		font-weight: 500;
		margin-bottom: 10rpx;
		display: block;
	}

	.status-desc {
		font-size: 14px;
		color: #999999;
	}

	.order-details {
		flex: 1;
		overflow: auto;
	}

	.address-section {
		background-color: #ffffff;
		padding: 30rpx;
		display: flex;
		align-items: flex-start;
		margin-bottom: 20rpx;
	}

	.address-icon {
		margin-right: 20rpx;
		padding-top: 6rpx;
	}

	.address-content {
		flex: 1;
	}

	.address-title {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 10rpx;
	}

	.location-text {
		font-size: 16px;
		color: #333333;
		font-weight: 500;
	}

	.distance {
		font-size: 14px;
		color: #666666;
	}

	.address-desc {
		font-size: 14px;
		color: #999999;
	}

	.order-items {
		background-color: #ffffff;
		padding: 30rpx;
		margin-bottom: 20rpx;
	}

	.item {
		display: flex;
		align-items: center;
		padding: 20rpx 0;
	}

	.item:not(:last-child) {
		border-bottom: 1px solid #f5f5f5;
	}

	.item-image {
		width: 120rpx;
		height: 120rpx;
		border-radius: 12rpx;
		margin-right: 20rpx;
	}

	.item-info {
		flex: 1;
	}

	.item-name {
		font-size: 14px;
		color: #333333;
		margin-bottom: 10rpx;
	}

	.item-price {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.price {
		font-size: 16px;
		color: #ff6b6b;
		font-weight: bold;
	}

	.time {
		font-size: 12px;
		color: #999999;
	}

	.order-info {
		background-color: #ffffff;
		padding: 30rpx;
	}

	.info-item {
		display: flex;
		justify-content: space-between;
		margin-bottom: 20rpx;
	}

	.label {
		font-size: 14px;
		color: #666666;
	}

	.value {
		font-size: 14px;
		color: #333333;
	}

	.price-value {
		color: #ff6b6b;
		font-weight: 500;
	}

	.tab-bar {
		height: 100rpx;
		background-color: #ffffff;
		display: flex;
		justify-content: space-around;
		align-items: center;
		border-top: 1px solid #f5f5f5;
		flex-shrink: 0;
	}

	.tab-item {
		display: flex;
		flex-direction: column;
		align-items: center;
	}

	.tab-text {
		font-size: 12px;
		color: #666666;
		margin-top: 6rpx;
	}

	.tab-text.active {
		color: #1296db;
	}

	/* 底部操作按钮样式优化 */
	.action-bar {
		display: flex;
		padding: 20rpx 30rpx;
		background-color: #ffffff;
		border-top: 1px solid #f5f5f5;
		gap: 20rpx;
	}

	.action-btn {
		flex: 1;
		height: 80rpx;
		line-height: 80rpx;
		border-radius: 40rpx;
		font-size: 16px;
		font-weight: 500;
		background-color: #1296db;
		color: #ffffff;
		border: none;
		transition: all 0.3s ease;
		/* 移除默认按钮样式 */
		padding: 0;
		margin: 0;
	}

	.action-btn:not(.cancel-btn):hover {
		background-color: #0e78b9;
		transform: translateY(-2rpx);
		box-shadow: 0 4rpx 10rpx rgba(18, 150, 219, 0.3);
	}

	.action-btn:not(.cancel-btn):active {
		transform: translateY(0);
		box-shadow: 0 2rpx 5rpx rgba(18, 150, 219, 0.2);
	}

	/* 取消按钮特殊样式 */
	.cancel-btn {
		background-color: #f5f5f5;
		color: #666666;
	}

	.cancel-btn:hover {
		background-color: #eeeeee;
		transform: translateY(-2rpx);
		box-shadow: 0 4rpx 10rpx rgba(0, 0, 0, 0.05);
	}

	.cancel-btn:active {
		transform: translateY(0);
		box-shadow: 0 2rpx 5rpx rgba(0, 0, 0, 0.03);
	}

	/* 加载和错误状态样式补充 */
	.loading-view {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: 60rpx 0;
	}

	.loading-text {
		font-size: 14px;
		color: #666666;
		margin-top: 20rpx;
	}

	.error-view {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: 60rpx 30rpx;
		text-align: center;
	}

	.error-text {
		font-size: 14px;
		color: #ff6b6b;
		margin-top: 20rpx;
		margin-bottom: 30rpx;
	}

	.retry-btn {
		height: 70rpx;
		line-height: 70rpx;
		padding: 0 40rpx;
		border-radius: 35rpx;
		background-color: #1296db;
		color: #ffffff;
		font-size: 14px;
		border: none;
	}
</style>
<template>
	<view class="order-page">
		<!-- 顶部选项卡和刷新按钮 -->
		<u-tabs :list="tabList" :current="currentTab" @change="changeTab">
			<template #right>
				<u-icon name="reload" size="21" bold @click="loadOrders()"/>
			</template>
		</u-tabs>


		<!-- 订单列表 -->
		<view class="order-list">
			<view class="order-item" v-for="order in filteredOrders" :key="order.oid" @click="navigateToDetail(order)">
				<view class="order-header">
					<text class="order-id">订单号：{{ order.oid }}</text>
					<text class="order-status" style="color: #FF9800;">待接单</text>
				</view>
				<view class="order-content">
					<image class="order-image" :src="getOrderImage(order)" mode="aspectFill"></image>
					<view class="order-info">
						<text class="order-title">{{ getOrderTypeText(order) }}</text>
						<text class="order-desc">{{ getOrderDesc(order) }}</text>
						<text class="order-price">¥{{ order.amount }}</text>
					</view>
				</view>
				<view class="order-footer">
					<text class="order-time">{{order.createTime }}</text>
					<view class="order-actions">
						<!-- <button class="action-btn" @click.stop="acceptOrder(order.oid)">接单</button> -->
						<u-button size="mini" type="primary" shape="circle">接单</u-button>
					</view>
				</view>
			</view>

			<view class="empty-tip" v-if="filteredOrders.length === 0">
				<text class="empty-text">暂无待接订单</text>
			</view>
		</view>

		<!-- 下拉刷新 -->
		<u-loading-page :loading="loading" bgColor="#f5f5f5"></u-loading-page>
	</view>
</template>

<script lang="ts" setup>
	import { ref, computed, onMounted } from 'vue';
	import { onLoad, onShow, onPullDownRefresh } from '@dcloudio/uni-app'
	import request from '@/utils/request.js'

	const currentTab = ref(0);
	const loading = ref(false);
	const orders = ref([]);

	// 选项卡配置
	const tabList = ref([
		{ name: '全部' },
		{ name: '快递代取' },
		{ name: '外卖代取' },
		{ name: '物品搬运' },
		{ name: '商品代购' }
	]);

	// 订单类型映射
	const orderTypeMap = {
		'E': { text: '快递代取', image: 'https://ai-public.mastergo.com/ai/img_res/53451dce5b9ad1abd6747fc75f050aa1.jpg' },
		'T': { text: '外卖代取', image: 'https://ai-public.mastergo.com/ai/img_res/7c254a7c6488aeb3d613034db4854725.jpg' },
		'C': { text: '物品搬运', image: 'https://ai-public.mastergo.com/ai/img_res/2a5e323ee652e3771b05ef5e72d4155c.jpg' },
		'P': { text: '商品代购', image: 'https://ai-public.mastergo.com/ai/img_res/dd794bd7156efaa64de362c009f59890.jpg' }
	};

	// 根据detail字段推断订单类型
	const inferOrderType = (order) => {
		if (!order.detail) return 'E';

		try {
			const detail = typeof order.detail === 'string' ? JSON.parse(order.detail) : order.detail;

			if (detail.company && detail.code) {
				return 'E';
			} else if (detail.location && detail.code && !detail.company) {
				return 'T';
			} else if (detail.description && detail.weight) {
				return 'C';
			} else if (detail.productLink && detail.description) {
				return 'P';
			} else if (detail.description && detail.count && detail.price) {
				return 'P';
			}
		} catch (e) {
			console.error('解析订单详情失败:', e);
		}

		return 'E';
	};

	// 加载订单列表
	const loadOrders = async () => {
		loading.value = true;
		try {
			const res = await request({
				url: '/order/list',
				method: 'GET'
			});
			const pendingOrders = (res.data || []).filter(order => order.status === 'D');
			orders.value = pendingOrders.map(order => ({
				...order,
				inferredType: inferOrderType(order)
			}));
		} catch (error) {
			console.error('加载订单失败:', error);
			uni.showToast({
				title: '加载订单失败',
				icon: 'none'
			});
		} finally {
			loading.value = false;
			uni.stopPullDownRefresh();
		}
	};

	// 根据订单获取图片
	const getOrderImage = (order) => {
		const orderType = order.order_type || order.inferredType;
		return orderTypeMap[orderType]?.image || orderTypeMap['E'].image;
	};

	// 根据订单获取类型文本
	const getOrderTypeText = (order) => {
		const orderType = order.order_type || order.inferredType;
		return orderTypeMap[orderType]?.text || '快递代取';
	};

	// 获取订单描述
	const getOrderDesc = (order) => {
		if (!order.detail) return order.remark || '暂无描述';

		try {
			const detail = typeof order.detail === 'string' ? JSON.parse(order.detail) : order.detail;
			if (detail.remark) return detail.remark;

			const orderType = order.order_type || order.inferredType;
			switch (orderType) {
				case 'E':
					return `${detail.company || ''} ${detail.code || ''}`;
				case 'T':
					return `${detail.location || ''} ${detail.code || ''}`;
				case 'C':
					return `${detail.description || ''} ${detail.weight || ''}`;
				case 'P':
					return detail.description || '商品代购';
				default:
					return '暂无描述';
			}
		} catch (e) {
			return order.remark || '暂无描述';
		}
	};

	// 格式化时间
	const formatTime = (time) => {
		if (!time) return '刚刚';
		const date = new Date(time);
		return `${date.getMonth() + 1}月${date.getDate()}日 ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
	};

	// 筛选订单
	const filteredOrders = computed(() => {
		if (currentTab.value === 0) return orders.value;

		const typeMap = {
			1: 'E', // 快递代取
			2: 'T', // 外卖代取
			3: 'C', // 物品搬运
			4: 'P'  // 商品代购
		};

		const targetType = typeMap[currentTab.value];
		return orders.value.filter(order => {
			const orderType = order.order_type || order.inferredType;
			return orderType === targetType;
		});
	});

	// 切换选项卡
	const changeTab = (item) => {
		currentTab.value = item.index;
	};

	// 刷新数据
	const handleRefresh = () => {
		loadOrders();
		uni.showToast({
			title: '刷新中...',
			icon: 'loading',
			duration: 1000
		});
	};

	// 跳转到订单详情页
	const navigateToDetail = (order : any) => {
		uni.navigateTo({
			url: `/pages/common/order-detail/order-detail?oid=${order.oid}`
		});
	};

	// 接单
	const acceptOrder = async (oid : string) => {
		try {
			await request({
				url: '/order/accept',
				method: 'POST',
				data: { oid }
			});
			uni.showToast({
				title: '接单成功',
				icon: 'success'
			});
			// 接单成功后从列表中移除
			orders.value = orders.value.filter(order => order.oid !== oid);
		} catch (error) {
			uni.showToast({
				title: '接单失败',
				icon: 'none'
			});
		}
	};

	onMounted(() => {
		loadOrders();
	});

	// 页面显示时刷新数据
	onShow(() => {
		loadOrders();
	});

	// 下拉刷新
	onPullDownRefresh(() => {
		loadOrders();
	});
</script>

<style scoped>
	.order-page {
		display: flex;
		flex-direction: column;
		height: 100vh;
		background-color: #f5f5f5;
	}

	.tab-header {
		display: flex;
		align-items: center;
		background-color: #fff;
		padding: 0 20rpx;
		border-bottom: 1rpx solid #eee;
		position: sticky;
		top: 0;
		z-index: 10;
	}

	.tabs-container {
		flex: 1;
	}

	.order-list {
		flex: 1;
		overflow-y: auto;
		padding: 20rpx;
	}

	.order-item {
		background-color: #fff;
		border-radius: 16rpx;
		padding: 30rpx;
		margin-bottom: 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
	}

	.order-header {
		display: flex;
		justify-content: space-between;
		margin-bottom: 20rpx;
	}

	.order-id {
		font-size: 26rpx;
		color: #999;
	}

	.order-status {
		font-size: 26rpx;
		font-weight: bold;
	}

	.order-content {
		display: flex;
		margin-bottom: 20rpx;
	}

	.order-image {
		width: 160rpx;
		height: 160rpx;
		border-radius: 12rpx;
		margin-right: 20rpx;
		flex-shrink: 0;
	}

	.order-info {
		flex: 1;
		display: flex;
		flex-direction: column;
	}

	.order-title {
		font-size: 30rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 10rpx;
		display: -webkit-box;
		-webkit-line-clamp: 1;
		-webkit-box-orient: vertical;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	.order-desc {
		font-size: 26rpx;
		color: #666;
		margin-bottom: 10rpx;
		display: -webkit-box;
		-webkit-line-clamp: 2;
		-webkit-box-orient: vertical;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	.order-price {
		font-size: 28rpx;
		font-weight: bold;
		color: #1a73e8;
	}

	.order-footer {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.order-time {
		font-size: 24rpx;
		color: #999;
	}

	.order-actions {
		display: flex;
	}

	/* 	.action-btn {
		font-size: 24rpx;
		padding: 0 30rpx;
		height: 60rpx;
		line-height: 60rpx;
		border-radius: 30rpx;
		background-color: #1a73e8;
		color: #fff;
	} */

	.empty-tip {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: 100rpx 0;
	}

	.empty-text {
		font-size: 28rpx;
		color: #999;
	}
</style>
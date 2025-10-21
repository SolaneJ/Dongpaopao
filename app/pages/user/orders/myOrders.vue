<!-- myOrders.vue -->
<template>
	<view class="order-page">
		<!-- 顶部选项卡和刷新按钮 -->
		<u-tabs :list="tabList" :current="currentTab" @change="changeTab">
			<template #right>
				<u-icon name="list" size="21" bold @click="showFilter = true" />
			</template>
		</u-tabs>

		<!-- 订单列表 -->
		<view class="order-list">
			<view class="order-item" v-for="order in filteredOrders" :key="order.oid" @click="navigateToDetail(order)">
				<view class="order-header">
					<view class="order-header-left">
						<text class="order-id">订单号：{{ order.oid }}</text>
						<view class="order-role-tag" :class="order.role === 'sender' ? 'sender-tag' : 'receiver-tag'">
							{{ order.role === 'sender' ? '我下单' : '我接单' }}
						</view>
					</view>
					<text class="order-status" :style="{ color: getStatusColor(order.status) }">
						{{ getStatusText(order.status) }}
					</text>
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
					<text class="order-time">{{ formatTime(order.createTime) }}</text>
					<view class="order-actions">
						<u-icon name="arrow-right-double" />
					</view>
				</view>
			</view>

			<view class="empty-tip" v-if="filteredOrders.length === 0">
				<text class="empty-text">暂无订单</text>
			</view>
		</view>

		<!-- 筛选弹窗 -->
		<u-popup :show="showFilter" mode="bottom" @close="showFilter = false">
			<view class="filter-popup">
				<view class="filter-header">
					<text class="filter-title">高级筛选</text>
					<u-icon name="close" size="20" @click="showFilter = false" />
				</view>

				<view class="filter-section">
					<text class="filter-label">订单状态</text>
					<view class="filter-options">
						<view class="filter-option" :class="{ active: tempFilterStatus === '' }"
							@click="tempFilterStatus = ''">
							全部状态
						</view>
						<view class="filter-option" :class="{ active: tempFilterStatus === 'D' }"
							@click="tempFilterStatus = 'D'">
							待接单
						</view>
						<view class="filter-option" :class="{ active: tempFilterStatus === 'P' }"
							@click="tempFilterStatus = 'J'">
							进行中
						</view>
						<view class="filter-option" :class="{ active: tempFilterStatus === 'C' }"
							@click="tempFilterStatus = 'S'">
							已完成
						</view>
						<view class="filter-option" :class="{ active: tempFilterStatus === 'X' }"
							@click="tempFilterStatus = 'C'">
							已取消
						</view>
					</view>
				</view>

				<view class="filter-section">
					<text class="filter-label">订单类型</text>
					<view class="filter-options">
						<view class="filter-option" :class="{ active: tempFilterType === '' }"
							@click="tempFilterType = ''">
							全部类型
						</view>
						<view class="filter-option" :class="{ active: tempFilterType === 'E' }"
							@click="tempFilterType = 'E'">
							快递代取
						</view>
						<view class="filter-option" :class="{ active: tempFilterType === 'T' }"
							@click="tempFilterType = 'T'">
							外卖代取
						</view>
						<view class="filter-option" :class="{ active: tempFilterType === 'C' }"
							@click="tempFilterType = 'C'">
							物品搬运
						</view>
						<view class="filter-option" :class="{ active: tempFilterType === 'P' }"
							@click="tempFilterType = 'P'">
							商品代购
						</view>
					</view>
				</view>

				<view class="filter-section">
					<text class="filter-label">订单角色</text>
					<view class="filter-options">
						<view class="filter-option" :class="{ active: tempFilterRole === '' }"
							@click="tempFilterRole = ''">
							全部角色
						</view>
						<view class="filter-option" :class="{ active: tempFilterRole === 'sender' }"
							@click="tempFilterRole = 'sender'">
							我下单的
						</view>
						<view class="filter-option" :class="{ active: tempFilterRole === 'receiver' }"
							@click="tempFilterRole = 'receiver'">
							我接单的
						</view>
					</view>
				</view>

				<view class="filter-actions">
					<view class="filter-btn reset" @click="handleResetFilter">
						重置
					</view>
					<view class="filter-btn confirm" @click="handleConfirmFilter">
						确认筛选
					</view>
				</view>
			</view>
		</u-popup>

		<!-- 下拉刷新 -->
		<u-loading-page :loading="loading" bgColor="#f5f5f5"></u-loading-page>
	</view>
</template>

<script lang="ts" setup>
	import { ref, computed } from 'vue';
	import { onShow, onPullDownRefresh } from '@dcloudio/uni-app'

	const currentTab = ref(0);
	const loading = ref(false);
	const orders = ref([]);
	const showFilter = ref(false);

	// 生效的筛选条件
	const filterStatus = ref('');
	const filterType = ref('');
	const filterRole = ref('');

	// 临时的筛选条件（用于弹窗内操作）
	const tempFilterStatus = ref('');
	const tempFilterType = ref('');
	const tempFilterRole = ref('');

	// 选项卡配置 - 添加了已取消状态
	const tabList = ref([
		{ name: '全部订单' },
		{ name: '待接单' },
		{ name: '进行中' },
		{ name: '已完成' },
		{ name: '已取消' }
	]);

	// 订单类型映射
	const orderTypeMap = {
		'E': { text: '快递代取', image: 'https://ai-public.mastergo.com/ai/img_res/53451dce5b9ad1abd6747fc75f050aa1.jpg' },
		'T': { text: '外卖代取', image: 'https://ai-public.mastergo.com/ai/img_res/7c254a7c6488aeb3d613034db4854725.jpg' },
		'C': { text: '物品搬运', image: 'https://ai-public.mastergo.com/ai/img_res/2a5e323ee652e3771b05ef5e72d4155c.jpg' },
		'P': { text: '商品代购', image: 'https://ai-public.mastergo.com/ai/img_res/dd794bd7156efaa64de362c009f59890.jpg' }
	};

	// 订单状态映射
	const orderStatusMap = {
		'D': { text: '待接单', color: '#FF9800' },
		'J': { text: '进行中', color: '#9C27B0' },
		'S': { text: '已完成', color: '#4CAF50' },
		'C': { text: '已取消', color: '#9E9E9E' }
	};

	// 生成模拟数据
	const generateMockData = () => {
		const mockOrders = [];
		const orderTypes = ['E', 'T', 'C', 'P'];
		const statuses = ['D', 'J', 'S', 'C'];
		const roles = ['sender', 'receiver'];

		for (let i = 1; i <= 20; i++) {
			const type = orderTypes[Math.floor(Math.random() * orderTypes.length)];
			const status = statuses[Math.floor(Math.random() * statuses.length)];
			const role = roles[Math.floor(Math.random() * roles.length)];

			mockOrders.push({
				oid: `ORD${Date.now()}${i}`,
				order_type: type,
				amount: (Math.random() * 50 + 5).toFixed(2),
				createTime: new Date(Date.now() - Math.random() * 7 * 24 * 60 * 60 * 1000),
				status: status,
				role: role,
				detail: generateMockDetail(type)
			});
		}

		return mockOrders;
	};

	// 根据订单类型生成模拟详情
	const generateMockDetail = (type) => {
		switch (type) {
			case 'E':
				return {
					company: ['顺丰', '中通', '圆通', '韵达'][Math.floor(Math.random() * 4)],
					code: Math.random().toString(36).substring(2, 8).toUpperCase(),
					remark: '请尽快送达，谢谢！'
				};
			case 'T':
				return {
					location: '北门外卖架',
					code: Math.random().toString(36).substring(2, 8).toUpperCase(),
					remark: '奶茶请小心不要洒'
				};
			case 'C':
				return {
					description: '书籍和杂物',
					weight: `${Math.floor(Math.random() * 10) + 1}kg`,
					remark: '请轻拿轻放'
				};
			case 'P':
				return {
					productLink: 'https://example.com/product',
					description: '零食和饮料',
					count: Math.floor(Math.random() * 5) + 1,
					price: (Math.random() * 30 + 10).toFixed(2),
					remark: '请尽快购买'
				};
			default:
				return { remark: '暂无描述' };
		}
	};

	// 加载订单列表
	const loadOrders = () => {
		loading.value = true;
		setTimeout(() => {
			orders.value = generateMockData();
			loading.value = false;
			uni.stopPullDownRefresh();
		}, 1000);
	};

	// 根据订单获取图片
	const getOrderImage = (order) => {
		const orderType = order.order_type;
		return orderTypeMap[orderType]?.image || orderTypeMap['E'].image;
	};

	// 根据订单获取类型文本
	const getOrderTypeText = (order) => {
		const orderType = order.order_type;
		return orderTypeMap[orderType]?.text || '快递代取';
	};

	// 获取订单状态文本
	const getStatusText = (status) => {
		return orderStatusMap[status]?.text || '未知状态';
	};

	// 获取订单状态颜色
	const getStatusColor = (status) => {
		return orderStatusMap[status]?.color || '#9E9E9E';
	};

	// 获取订单描述
	const getOrderDesc = (order) => {
		if (!order.detail) return '暂无描述';

		try {
			const detail = order.detail;
			if (detail.remark) return detail.remark;

			const orderType = order.order_type;

			switch (orderType) {
				case 'E':
					return `${detail.company || ''} ${detail.code ? '取件码：****' : ''}`;
				case 'T':
					return `${detail.location || ''} ${detail.code ? '取件码：****' : ''}`;
				case 'C':
					return `${detail.description || ''} ${detail.weight || ''}`;
				case 'P':
					return detail.description || '商品代购';
				default:
					return '暂无描述';
			}
		} catch (e) {
			return '暂无描述';
		}
	};

	// 格式化时间
	const formatTime = (time) => {
		if (!time) return '刚刚';
		if (typeof time === 'string' && time.includes('月')) return time;
		const date = new Date(time);
		return `${date.getMonth() + 1}月${date.getDate()}日 ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
	};

	// 筛选订单 - 只使用生效的筛选条件
	const filteredOrders = computed(() => {
		let filtered = orders.value;

		// 按顶部选项卡筛选
		if (currentTab.value !== 0) {
			const tabStatusMap = {
				1: 'D', // 待接单
				2: 'P', // 进行中
				3: 'C', // 已完成
				4: 'X'  // 已取消
			};
			const targetStatus = tabStatusMap[currentTab.value];
			filtered = filtered.filter(order => order.status === targetStatus);
		}

		// 按生效的高级筛选条件筛选
		if (filterStatus.value) {
			filtered = filtered.filter(order => order.status === filterStatus.value);
		}

		if (filterType.value) {
			filtered = filtered.filter(order => order.order_type === filterType.value);
		}

		if (filterRole.value) {
			filtered = filtered.filter(order => order.role === filterRole.value);
		}

		return filtered;
	});

	// 切换选项卡
	const changeTab = (item) => {
		currentTab.value = item.index;
	};

	// 打开筛选弹窗时，将生效的筛选条件复制到临时条件
	const openFilterPopup = () => {
		tempFilterStatus.value = filterStatus.value;
		tempFilterType.value = filterType.value;
		tempFilterRole.value = filterRole.value;
		showFilter.value = true;
	};

	// 重置筛选（重置临时条件）
	const handleResetFilter = () => {
		tempFilterStatus.value = '';
		tempFilterType.value = '';
		tempFilterRole.value = '';
	};

	// 确认筛选（将临时条件应用到生效条件）
	const handleConfirmFilter = () => {
		filterStatus.value = tempFilterStatus.value;
		filterType.value = tempFilterType.value;
		filterRole.value = tempFilterRole.value;
		showFilter.value = false;
		// 重置顶部选项卡到"全部订单"
		currentTab.value = 0;
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
	const navigateToDetail = (order) => {
		uni.navigateTo({
			url: `/pages/common/order-detail/order-detail?oid=${order.oid}&orderType=${order.order_type}`
		});
	};

	// 页面显示时加载数据
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
		align-items: center;
		margin-bottom: 20rpx;
	}

	.order-header-left {
		display: flex;
		align-items: center;
	}

	.order-id {
		font-size: 26rpx;
		color: #999;
	}

	.order-role-tag {
		margin-left: 16rpx;
		padding: 4rpx 12rpx;
		border-radius: 20rpx;
		font-size: 20rpx;
		font-weight: bold;
	}

	.sender-tag {
		background-color: #e3f2fd;
		color: #1976d2;
	}

	.receiver-tag {
		background-color: #e8f5e9;
		color: #388e3c;
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

	/* 筛选弹窗样式 */
	.filter-popup {
		padding: 40rpx;
		background: #fff;
		border-radius: 24rpx 24rpx 0 0;
	}

	.filter-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 40rpx;
	}

	.filter-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
	}

	.filter-section {
		margin-bottom: 40rpx;
	}

	.filter-label {
		display: block;
		font-size: 28rpx;
		color: #333;
		margin-bottom: 20rpx;
		font-weight: 500;
	}

	.filter-options {
		display: flex;
		flex-wrap: wrap;
		gap: 20rpx;
	}

	.filter-option {
		padding: 16rpx 24rpx;
		border: 2rpx solid #e0e0e0;
		border-radius: 30rpx;
		font-size: 26rpx;
		color: #666;
		background: #f8f8f8;
		transition: all 0.3s;
	}

	.filter-option.active {
		background: #1a73e8;
		color: #fff;
		border-color: #1a73e8;
	}

	.filter-actions {
		display: flex;
		gap: 20rpx;
		margin-top: 40rpx;
	}

	.filter-btn {
		flex: 1;
		padding: 24rpx;
		border-radius: 12rpx;
		font-size: 28rpx;
		font-weight: 500;
		text-align: center;
	}

	.filter-btn.reset {
		background: #f5f5f5;
		color: #666;
		border: 2rpx solid #e0e0e0;
	}

	.filter-btn.confirm {
		background: #1a73e8;
		color: #fff;
	}
</style>
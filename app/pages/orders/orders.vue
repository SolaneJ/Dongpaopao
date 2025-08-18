<template>
	<view class="order-page">
		<!-- 顶部筛选区 -->
		<view class="filter-container">
			<view class="filter-item" :class="{ active: filterIndex === 0 }" @click="changeFilter(0)">
				<text>全部</text>
			</view>
			<view class="filter-item" :class="{ active: filterIndex === 1 }" @click="changeFilter(1)">
				<text>待接单</text>
			</view>
			<view class="filter-item" :class="{ active: filterIndex === 2 }" @click="changeFilter(2)">
				<text>进行中</text>
			</view>
			<view class="filter-item" :class="{ active: filterIndex === 3 }" @click="changeFilter(3)">
				<text>已完成</text>
			</view>
			<view class="filter-item" :class="{ active: filterIndex === 4 }" @click="changeFilter(4)">
				<text>已取消</text>
			</view>
		</view>

		<!-- 订单列表 -->

		<view class="order-item" v-for="order in filteredOrders" :key="order.id" @click="navigateToDetail(order)">
			<view class="order-header">
				<text class="order-id">订单号：{{ order.orderNo }}</text>
				<text class="order-status" :style="{ color: getStatusColor(order.status) }">{{ order.status }}</text>
			</view>
			<view class="order-content">
				<image class="order-image" :src="order.image" mode="aspectFill"></image>
				<view class="order-info">
					<text class="order-title">{{ order.title }}</text>
					<text class="order-desc">{{ order.desc }}</text>
					<text class="order-price">¥{{ order.price }}</text>
				</view>
			</view>
			<view class="order-footer">
				<text class="order-time">{{ order.time }}</text>
				<view class="order-actions">
					<button class="action-btn" v-if="order.status === '待接单'"
						@click.stop="acceptOrder(order.id)">接单</button>
					<button class="action-btn" v-if="order.status === '进行中'"
						@click.stop="completeOrder(order.id)">完成</button>
					<button class="action-btn cancel-btn" v-if="order.status === '待接单' || order.status === '进行中'"
						@click.stop="cancelOrder(order.id)">取消</button>
					<button class="action-btn" v-if="order.status === '已完成'"
						@click.stop="viewOrderDetail(order.id)">查看详情</button>
				</view>
			</view>
		</view>
		<view class="empty-tip" v-if="filteredOrders.length === 0">
			<image class="empty-image"
				src="https://ai-public.mastergo.com/ai/img_res/999996a21d192526cccc6da403e6f6e5.jpg" mode="aspectFit">
			</image>
			<text class="empty-text">暂无相关订单</text>
		</view>
	</view>
</template>

<script lang="ts" setup>
	import { ref, computed } from 'vue';

	const filterIndex = ref(0);
	const orders = ref([
		{
			id: 1,
			orderNo: 'DD20230501001',
			title: '代取快递到宿舍',
			desc: '3个快递，从菜鸟驿站到3号宿舍楼',
			price: '8.00',
			time: '2023-05-01 10:30',
			status: '已完成',
			image: 'https://ai-public.mastergo.com/ai/img_res/53451dce5b9ad1abd6747fc75f050aa1.jpg',
			from: '菜鸟驿站',
			to: '3号宿舍楼',
			distance: '500m',
			completionTime: '03月15日 14:55',
			paymentMethod: '微信支付'
		},
		{
			id: 2,
			orderNo: 'DD20230502002',
			title: '代买奶茶',
			desc: '喜茶多肉葡萄，送到图书馆3楼',
			price: '5.00',
			time: '2023-05-02 15:20',
			status: '已完成',
			image: 'https://ai-public.mastergo.com/ai/img_res/7c254a7c6488aeb3d613034db4854725.jpg',
			from: '喜茶店',
			to: '图书馆3楼',
			distance: '800m',
			completionTime: '03月16日 15:45',
			paymentMethod: '支付宝'
		},
		{
			id: 3,
			orderNo: 'DD20230503003',
			title: '搬运行李',
			desc: '从5号楼到校门口，2个行李箱',
			price: '15.00',
			time: '2023-05-03 09:10',
			status: '进行中',
			image: 'https://ai-public.mastergo.com/ai/img_res/2a5e323ee652e3771b05ef5e72d4155c.jpg',
			from: '5号楼',
			to: '校门口',
			distance: '1.2km',
			completionTime: '预计30分钟内',
			paymentMethod: '微信支付'
		},
		{
			id: 4,
			orderNo: 'DD20230504004',
			title: '代买药品',
			desc: '感冒药和退烧药，送到医务室',
			price: '10.00',
			time: '2023-05-04 14:30',
			status: '待接单',
			image: 'https://ai-public.mastergo.com/ai/img_res/697c49898c235ab290d03b93a1548c74.jpg',
			from: '校医院',
			to: '医务室',
			distance: '300m',
			completionTime: '待接单',
			paymentMethod: '微信支付'
		},
		{
			id: 5,
			orderNo: 'DD20230505005',
			title: '作业辅导',
			desc: '数据结构算法题讲解',
			price: '30.00',
			time: '2023-05-05 19:00',
			status: '已取消',
			image: 'https://ai-public.mastergo.com/ai/img_res/dd794bd7156efaa64de362c009f59890.jpg',
			from: '线上',
			to: '线上',
			distance: '0m',
			completionTime: '已取消',
			paymentMethod: '微信支付'
		}
	]);

	const filterStatus = ['全部', '待接单', '进行中', '已完成', '已取消'];
	const filteredOrders = computed(() => {
		if (filterIndex.value === 0) return orders.value;
		return orders.value.filter(order => {
			if (filterIndex.value === 1) return order.status === '待接单';
			if (filterIndex.value === 2) return order.status === '进行中';
			if (filterIndex.value === 3) return order.status === '已完成';
			if (filterIndex.value === 4) return order.status === '已取消';
			return true;
		});
	});

	const changeFilter = (index : number) => {
		filterIndex.value = index;
	};

	const getStatusColor = (status : string) => {
		switch (status) {
			case '待接单': return '#FF9800';
			case '进行中': return '#2196F3';
			case '已完成': return '#4CAF50';
			case '已取消': return '#9E9E9E';
			default: return '#333';
		}
	};

	// 模拟请求订单详情
	const fetchOrderDetail = (id : number) => {
		return new Promise((resolve) => {
			setTimeout(() => {
				const order = orders.value.find(o => o.id === id);
				resolve(order || null);
			}, 300);
		});
	};

	// 跳转到订单详情页
	const navigateToDetail = (order : any) => {
		// 拼接所有字段到 url
		const query = Object.entries(order)
			.map(([key, value]) => `${encodeURIComponent(key)}=${encodeURIComponent(value)}`)
			.join('&');
		uni.navigateTo({
			url: `/pages/common/order-detail/order-detail?${query}`
		});
	};

	const acceptOrder = (id : number) => {
		const order = orders.value.find(o => o.id === id);
		if (order) {
			order.status = '进行中';
			uni.showToast({
				title: '接单成功',
				icon: 'success'
			});
		}
	};

	const completeOrder = (id : number) => {
		const order = orders.value.find(o => o.id === id);
		if (order) {
			order.status = '已完成';
			uni.showToast({
				title: '订单已完成',
				icon: 'success'
			});
		}
	};

	const cancelOrder = (id : number) => {
		uni.showModal({
			title: '提示',
			content: '确定要取消该订单吗？',
			success: (res) => {
				if (res.confirm) {
					const order = orders.value.find(o => o.id === id);
					if (order) {
						order.status = '已取消';
						uni.showToast({
							title: '订单已取消',
							icon: 'success'
						});
					}
				}
			}
		});
	};

	const viewOrderDetail = (id : number) => {
		navigateToDetail(orders.value.find(o => o.id === id));
	};
</script>

<style>
	page {
		height: 100%;
		background-color: #f5f5f5;
		box-sizing: border-box;
	}

	.order-page {
		display: flex;
		flex-direction: column;
		height: 100%;
		padding-bottom: 100rpx;
	}

	.filter-container {
		display: flex;
		background-color: #fff;
		padding: 20rpx 0;
		border-bottom: 1rpx solid #eee;
		position: sticky;
		top: 0;
		z-index: 10;
	}

	.filter-item {
		flex: 1;
		text-align: center;
		padding: 20rpx 0;
		font-size: 28rpx;
		color: #666;
		position: relative;
	}

	.filter-item.active {
		color: #1a73e8;
		font-weight: bold;
	}

	.filter-item.active::after {
		content: '';
		position: absolute;
		bottom: 0;
		left: 50%;
		transform: translateX(-50%);
		width: 60rpx;
		height: 4rpx;
		background-color: #1a73e8;
		border-radius: 2rpx;
	}

	.order-list {
		flex: 1;
		padding: 20rpx;
		box-sizing: border-box;
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

	.action-btn {
		margin-left: 20rpx;
		font-size: 24rpx;
		padding: 0 20rpx;
		height: 50rpx;
		line-height: 50rpx;
		border-radius: 25rpx;
		background-color: #1a73e8;
		color: #fff;
	}

	.cancel-btn {
		background-color: #fff;
		color: #666;
		border: 1rpx solid #ddd;
	}

	.empty-tip {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: 100rpx 0;
	}

	.empty-image {
		width: 300rpx;
		height: 300rpx;
		margin-bottom: 40rpx;
	}

	.empty-text {
		font-size: 28rpx;
		color: #999;
	}
</style>
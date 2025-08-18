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
		<!-- 导航条scroll-y -->
		<scroll-view class="order-list" >
			<view class="order-item" v-for="order in filteredOrders" :key="order.id">
				<view class="order-header">
					<text class="order-id">订单号：{{ order.orderNo }}</text>
					<text class="order-status"
						:style="{ color: getStatusColor(order.status) }">{{ order.status }}</text>
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
							@click="acceptOrder(order.id)">接单</button>
						<button class="action-btn" v-if="order.status === '进行中'"
							@click="completeOrder(order.id)">完成</button>
						<button class="action-btn cancel-btn" v-if="order.status === '待接单' || order.status === '进行中'"
							@click="cancelOrder(order.id)">取消</button>
						<button class="action-btn" v-if="order.status === '已完成'"
							@click="viewOrderDetail(order.id)">查看详情</button>
					</view>
				</view>
			</view>
			<view class="empty-tip" v-if="filteredOrders.length === 0">
				<image class="empty-image"
					src="https://ai-public.mastergo.com/ai/img_res/999996a21d192526cccc6da403e6f6e5.jpg"
					mode="aspectFit"></image>
				<text class="empty-text">暂无相关订单</text>
			</view>
		</scroll-view>
	</view>
</template>
<script lang="ts" setup>
import { ref, computed, onMounted } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
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
		image: 'https://ai-public.mastergo.com/ai/img_res/53451dce5b9ad1abd6747fc75f050aa1.jpg'
	},
	{
		id: 2,
		orderNo: 'DD20230502002',
		title: '代买奶茶',
		desc: '喜茶多肉葡萄，送到图书馆3楼',
		price: '5.00',
		time: '2023-05-02 15:20',
		status: '已完成',
		image: 'https://ai-public.mastergo.com/ai/img_res/7c254a7c6488aeb3d613034db4854725.jpg'
	},
	{
		id: 3,
		orderNo: 'DD20230503003',
		title: '搬运行李',
		desc: '从5号楼到校门口，2个行李箱',
		price: '15.00',
		time: '2023-05-03 09:10',
		status: '进行中',
		image: 'https://ai-public.mastergo.com/ai/img_res/2a5e323ee652e3771b05ef5e72d4155c.jpg'
	},
	{
		id: 4,
		orderNo: 'DD20230504004',
		title: '代买药品',
		desc: '感冒药和退烧药，送到医务室',
		price: '10.00',
		time: '2023-05-04 14:30',
		status: '待接单',
		image: 'https://ai-public.mastergo.com/ai/img_res/697c49898c235ab290d03b93a1548c74.jpg'
	},
	{
		id: 5,
		orderNo: 'DD20230505005',
		title: '作业辅导',
		desc: '数据结构算法题讲解',
		price: '30.00',
		time: '2023-05-05 19:00',
		status: '已取消',
		image: 'https://ai-public.mastergo.com/ai/img_res/dd794bd7156efaa64de362c009f59890.jpg'
	},
	{
		id: 6,
		orderNo: 'DD20230506006',
		title: '代买早餐',
		desc: '豆浆油条，送到教学楼201',
		price: '5.00',
		time: '2023-05-06 07:30',
		status: '已完成',
		image: 'https://ai-public.mastergo.com/ai/img_res/b180edb4a3f0d0563db8e4b6d1269ffc.jpg'
	},
	{
		id: 7,
		orderNo: 'DD20230507007',
		title: '代打印资料',
		desc: '20页A4纸，送到实验室',
		price: '3.00',
		time: '2023-05-07 13:45',
		status: '已完成',
		image: 'https://ai-public.mastergo.com/ai/img_res/23d83c34741766d1d3d6def0d2f3a04a.jpg'
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
	uni.navigateTo({
		url: `/pages/order-detail/index?id=${id}`
	});
};
onLoad((options) => {
  if (options.tab === 'active') {
    filterIndex.value = 2; // “进行中”标签索引
  }
});
</script>
<style>
	page {
		height: 100%;
		background-color: #f5f5f5;
		box-sizing: border-box; /* 确保盒模型计算正确 */
	}
	/* .order - list ::-webkit-scrollbar {
	  width: 0;
	  height: 0;
	  display: none;
	} */

	.order-page {
		display: flex;
		flex-direction: column;
		height: 100%;
		padding-bottom: 100rpx;
	}

	/* 筛选区样式 */
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
		height: 4rpx;
		background-color: #1a73e8;
		border-radius: 2rpx;
	}

	/* 订单列表样式 */
	.order-list {
		flex: 1;
		padding: 20rpx;
		box-sizing: border-box; /* 确保内边距不增加额外宽度 */
	}

	.order-item {
		background-color: #fff;
		border-radius: 16rpx;
		padding: 30rpx;
		margin-bottom: 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
		margin-left: 0; /* 移除左侧边距 */
		margin-right: 0; /* 移除右侧边距 */
		width: 100%; /* 明确设置宽度 */
		box-sizing: border-box; /* 确保内边距不增加额外宽度 */
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

	/* 空状态样式 */
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
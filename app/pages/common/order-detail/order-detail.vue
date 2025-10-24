<template>
	<view class="order-detail-page">
		<view class="detail-content">
			<!-- 订单状态卡片 -->
			<view class="status-card">
				<view class="status-info">
					<text class="status-text">{{ getStatusText(orderDetail.status) }}</text>
					<text class="order-type">{{ getOrderTypeText(orderDetail.orderType) }}</text>
				</view>
				<view class="order-id">订单号：{{ orderDetail.oid }}</view>
			</view>

			<!-- 订单基本信息 -->
			<view class="info-card">
				<view class="card-title">订单信息</view>
				<view class="info-item">
					<text class="label">下单时间</text>
					<text class="value">{{ formatTime(orderDetail.createTime) }}</text>
				</view>
				<view class="info-item">
					<text class="label">订单金额</text>
					<text class="value price">¥{{ orderDetail.amount }}</text>
				</view>
				<view class="info-item" v-if="orderDetail.completeTime">
					<text class="label">完成时间</text>
					<text class="value">{{ formatTime(orderDetail.completeTime) }}</text>
				</view>
				<view class="info-item" v-if="orderDetail.expectTime">
					<text class="label">期望送达时间</text>
					<text class="value">{{ formatTime(orderDetail.expectTime) }}</text>
				</view>
				<view class="info-item" v-if="orderDetail.acceptTime">
					<text class="label">接单时间</text>
					<text class="value">{{ formatTime(orderDetail.acceptTime) }}</text>
				</view>
			</view>

			<!-- 业务详情信息 -->
			<view class="info-card" v-if="Object.keys(businessDetail).length > 0">
				<view class="card-title">{{ getOrderTypeText(orderDetail.orderType) }}详情</view>

				<!-- 快递代取 -->
				<view v-if="orderDetail.orderType === 'E'">
					<view class="info-item">
						<text class="label">快递公司</text>
						<text class="value">{{ businessDetail.company || '未知' }}</text>
					</view>
					<view class="info-item" v-if="showSensitiveInfo">
						<text class="label">取件码</text>
						<text class="value">{{ businessDetail.code || '无' }}</text>
					</view>
					<view class="info-item">
						<text class="label">存放位置</text>
						<text class="value">{{ businessDetail.location || '未知' }}</text>
					</view>
				</view>

				<!-- 外卖代取 -->
				<view v-else-if="orderDetail.orderType === 'T'">
					<view class="info-item">
						<text class="label">取餐点</text>
						<text class="value">{{ businessDetail.location || '未知' }}</text>
					</view>
					<view class="info-item" v-if="showSensitiveInfo">
						<text class="label">取件码</text>
						<text class="value">{{ businessDetail.code || '无' }}</text>
					</view>
				</view>

				<!-- 物品搬运 -->
				<view v-else-if="orderDetail.orderType === 'C'">
					<view class="info-item">
						<text class="label">物品描述</text>
						<text class="value">{{ businessDetail.description || '无' }}</text>
					</view>
					<view class="info-item">
						<text class="label">预估重量</text>
						<text class="value">{{ businessDetail.weight || '未知' }}</text>
					</view>
					<view class="info-item">
						<text class="label">物品数量</text>
						<text class="value">{{ businessDetail.count || 1 }}</text>
					</view>
				</view>

				<!-- 商品代购 -->
				<view v-else-if="orderDetail.orderType === 'P'">
					<view class="info-item">
						<text class="label">商品描述</text>
						<text class="value">{{ businessDetail.description || '无' }}</text>
					</view>
					<view class="info-item">
						<text class="label">商品链接</text>
						<text class="value link">{{ businessDetail.productLink || '无' }}</text>
					</view>
					<view class="info-item">
						<text class="label">购买数量</text>
						<text class="value">{{ businessDetail.count || 1 }}</text>
					</view>
					<view class="info-item" v-if="businessDetail.price">
						<text class="label">商品价格</text>
						<text class="value">¥{{ businessDetail.price }}</text>
					</view>
				</view>

				<!-- 通用备注信息 -->
				<view class="info-item" v-if="businessDetail.remark">
					<text class="label">备注信息</text>
					<text class="value">{{ businessDetail.remark }}</text>
				</view>
			</view>

			<!-- 配送信息 -->
			<view class="info-card" v-if="addressInfo">
				<view class="card-title">配送信息</view>
				<view class="info-item">
					<text class="label">收件人</text>
					<text class="value">{{ addressInfo.sjr }}</text>
				</view>
				<view class="info-item">
					<text class="label">配送地址</text>
					<text class="value">{{ addressInfo.detail }}</text>
				</view>
			</view>

			<!-- 操作按钮 -->
			<view class="action-buttons" v-if="showActionButtons">
				<u-button v-if="orderDetail.status === 'J'" type="success" shape="circle"
					@click="goToPrivateChat(conversationId)">私聊</u-button>
				<u-button v-if="orderDetail.status === 'D' && orderDetail.xdr != userInfo.uid" type="primary"
					shape="circle" :loading="acceptLoading" @click="acceptOrder">接单</u-button>
				<u-button v-if="orderDetail.status === 'J'" type="warning" shape="circle" :loading="completeLoading"
					@click="completeOrder">完成订单</u-button>
				<u-button v-if="orderDetail.status === 'D' && orderDetail.xdr == userInfo.uid" type="error"
					shape="circle" plain :loading="cancelLoading" @click="cancelOrder">取消订单</u-button>
			</view>
		</view>

		<!-- 加载状态 -->
		<u-loading-page :loading="loading" bgColor="#f5f5f5" />
	</view>
</template>

<script lang="ts" setup>
	import { ref, computed } from 'vue';
	import { onLoad } from '@dcloudio/uni-app'
	import request from '@/utils/request.js'
	import { useUserStore } from '@/stores/user';

	const loading = ref(false);
	const acceptLoading = ref(false);
	const completeLoading = ref(false);
	const cancelLoading = ref(false);

	const userInfo = useUserStore().info

	const orderDetail = ref({
		oid: '',
		xdr: '',
		createTime: '',
		expectTime: null,
		orderType: '',
		aid: '',
		detail: '',
		amount: 0,
		jdr: null,
		acceptTime: null,
		completeTime: null,
		status: 'D'
	});
	const businessDetail = ref({});
	const addressInfo = ref(null);
	const conversationId = ref(null)

	// 计算属性
	const showActionButtons = computed(() => {
		return ['D', 'J'].includes(orderDetail.value.status);
	});

	// 是否显示敏感信息（取件码）
	const showSensitiveInfo = computed(() => {
		return ['J', 'S'].includes(orderDetail.value.status);
	});

	// 订单类型映射
	const orderTypeMap = {
		'E': '快递代取',
		'T': '外卖代取',
		'C': '物品搬运',
		'P': '商品代购'
	};

	// 状态映射
	const statusMap = {
		'D': '待接单',
		'J': '进行中',
		'S': '已完成',
		'C': '已取消'
	};

	onLoad((options) => {
		const { oid } = options;
		if (oid) {
			loadOrderDetail(oid);
		} else {
			uni.showToast({
				title: '订单ID不存在',
				icon: 'none'
			});
			uni.navigateBack();
		}
	});

	// 加载订单详情
	const loadOrderDetail = async (oid : string) => {
		loading.value = true;
		try {
			const res = await request({
				url: `/order/detail?oid=${oid}`,
				method: 'GET'
			});

			if (res.errCode === 0) {
				// 设置订单详情
				orderDetail.value = res.data.order;

				// 设置地址信息
				addressInfo.value = res.data.address;

				// 会话消息
				conversationId.value = res.data.conversationId;

				// 解析业务详情
				if (orderDetail.value.detail) {
					try {
						businessDetail.value = typeof orderDetail.value.detail === 'string'
							? JSON.parse(orderDetail.value.detail)
							: orderDetail.value.detail;
					} catch (e) {
						businessDetail.value = {};
					}
				}
			} else {
				throw new Error(res.msg || '获取订单详情失败');
			}
		} catch (error) {
			uni.showToast({
				title: '加载订单详情失败',
				icon: 'none'
			});
			setTimeout(() => {
				uni.navigateBack();
			}, 1500);
		} finally {
			loading.value = false;
		}
	};

	// 获取订单类型文本
	const getOrderTypeText = (orderType : string) => {
		return orderTypeMap[orderType] || '未知类型';
	};

	// 获取状态文本
	const getStatusText = (status : string) => {
		return statusMap[status] || '未知状态';
	};

	// 格式化时间
	const formatTime = (time : any) => {
		if (!time) return '-';
		const date = new Date(time);
		return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
	};

	// 接单操作
	const acceptOrder = async () => {
		if (acceptLoading.value) return;

		acceptLoading.value = true;
		try {
			const uid = userInfo.uid

			const res = await request({
				url: '/order/accept',
				method: 'PUT',
				data: {
					uid: uid,
					oid: orderDetail.value.oid
				}
			});

			if (res.errCode === 0) {
				uni.showToast({
					title: '接单成功',
					icon: 'success'
				});
				// 更新订单状态
				orderDetail.value.status = 'J';
				orderDetail.value.jdr = userInfo.nickname;
				orderDetail.value.acceptTime = new Date().toISOString();

				// 接单成功后创建私聊会话
				request({
					url: '/conversation/create',
					method: 'POST',
					data: {
						uid: uid,
						oid: orderDetail.value.oid
					},
				}).then((res) => {
					conversationId.value = res.data
				});
			} else if (res.errCode === 11) {
				// 抢单失败
				uni.showToast({
					title: '抢单失败，订单已被其他用户接取',
					icon: 'none',
					duration: 3000
				});
				// 刷新页面获取最新状态
				loadOrderDetail(orderDetail.value.oid);
			} else {
				throw new Error(res.msg || '接单失败');
			}
		} catch (error) {
			uni.showToast({
				title: error.message || '接单失败',
				icon: 'none'
			});
		} finally {
			acceptLoading.value = false;
		}
	};

	// 完成订单
	const completeOrder = async () => {
		if (completeLoading.value) return;

		try {
			uni.showModal({
				title: '确认完成',
				content: '确认订单已完成？',
				success: async (res) => {
					if (res.confirm) {
						completeLoading.value = true;

						try {
							const result = await request({
								url: '/order/status',
								method: 'PUT',
								data: {
									oid: orderDetail.value.oid,
									status: 'S'
								}
							});

							if (result.errCode === 0) {
								uni.showToast({
									title: '订单已完成',
									icon: 'success'
								});
								orderDetail.value.status = 'S';
								orderDetail.value.completeTime = new Date().toISOString();
							} else {
								throw new Error(result.msg || '操作失败');
							}
						} catch (error) {
							uni.showToast({
								title: error.message || '操作失败',
								icon: 'none'
							});
						} finally {
							completeLoading.value = false;
						}
					}
				}
			});
		} catch (error) {
			uni.showToast({
				title: error.message || '操作失败',
				icon: 'none'
			});
		}
	};

	// 取消订单
	const cancelOrder = async () => {
		if (cancelLoading.value) return;

		try {
			uni.showModal({
				title: '确认取消',
				content: '确定要取消这个订单吗？',
				success: async (res) => {
					if (res.confirm) {
						cancelLoading.value = true;

						try {
							const result = await request({
								url: '/order/status',
								method: 'PUT',
								data: {
									oid: orderDetail.value.oid,
									status: 'C'
								}
							});

							if (result.errCode === 0) {
								uni.showToast({
									title: '订单已取消',
									icon: 'success'
								});
								orderDetail.value.status = 'C';
							} else {
								throw new Error(result.msg || '取消失败');
							}
						} catch (error) {
							console.error('取消订单失败:', error);
							uni.showToast({
								title: error.message || '取消失败',
								icon: 'none'
							});
						} finally {
							cancelLoading.value = false;
						}
					}
				}
			});
		} catch (error) {
			console.error('取消订单失败:', error);
			uni.showToast({
				title: error.message || '取消失败',
				icon: 'none'
			});
		}
	};

	// 私聊
	const goToPrivateChat = (cid) => {
		uni.navigateTo({
			url: "/pages/messages/private-chat/private-chat?cid=" + cid
		})
	}
</script>

<style scoped>
	.order-detail-page {
		min-height: 100vh;
		background-color: #f5f5f5;
	}

	.detail-content {
		padding: 20rpx;
	}

	.status-card {
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		border-radius: 16rpx;
		padding: 40rpx 30rpx;
		color: white;
		margin-bottom: 20rpx;
	}

	.status-info {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 20rpx;
	}

	.status-text {
		font-size: 36rpx;
		font-weight: bold;
	}

	.order-type {
		font-size: 28rpx;
		background: rgba(255, 255, 255, 0.2);
		padding: 8rpx 16rpx;
		border-radius: 20rpx;
	}

	.order-id {
		font-size: 26rpx;
		opacity: 0.9;
	}

	.info-card {
		background-color: #fff;
		border-radius: 16rpx;
		padding: 30rpx;
		margin-bottom: 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
	}

	.card-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 30rpx;
		padding-bottom: 20rpx;
		border-bottom: 1rpx solid #eee;
	}

	.info-item {
		display: flex;
		justify-content: space-between;
		align-items: flex-start;
		margin-bottom: 24rpx;
	}

	.label {
		font-size: 28rpx;
		color: #666;
		flex-shrink: 0;
		width: 200rpx;
	}

	.value {
		font-size: 28rpx;
		color: #333;
		flex: 1;
		text-align: right;
		word-break: break-all;
	}

	.value.price {
		color: #1a73e8;
		font-weight: bold;
	}

	.value.link {
		color: #1a73e8;
		text-decoration: underline;
	}

	.action-buttons {
		padding: 30rpx 20rpx;
		background-color: #fff;
		position: sticky;
		bottom: 0;
		display: flex;
		flex-direction: column;
		gap: 20rpx;
	}
</style>
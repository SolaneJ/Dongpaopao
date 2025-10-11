<template>
	<view class="card">
		<u-title>代购信息</u-title>
		<!-- 商品链接 -->
		<view class="input-group">
			<text class="input-label">商品链接</text>
			<textarea v-model="businessInfo.productLink" placeholder="请粘贴商品链接" class="textarea-field" />
		</view>
		<!-- 商品描述 -->
		<view class="input-group">
			<text class="input-label">商品描述</text>
			<textarea v-model="businessInfo.description" placeholder="请描述商品的具体规格、型号、颜色等信息" class="textarea-field" />
		</view>
		<!-- 商品数量 -->
		<view class="input-group">
			<text class="input-label">购买数量</text>
			<view class="counter-wrapper">
				<uni-icons type="minus" size="20" color="#999" @click="decreaseCount" />
				<text class="counter-number">{{businessInfo.count}}</text>
				<uni-icons type="plus" size="20" color="#999" @click="increaseCount" />
			</view>
		</view>
		<!-- 商品价格 -->
		<view class="input-group">
			<text class="input-label">商品价格</text>
			<input v-model="businessInfo.price" type="digit" placeholder="请输入商品价格" class="input-field" />
		</view>
		<!-- 备注 -->
		<view class="input-group">
			<text class="input-label">备注信息</text>
			<textarea v-model="businessInfo.remark" placeholder="可填写商品相关的特殊要求（选填）" class="textarea-field" />
		</view>
	</view>
</template>

<script lang="ts" setup>
	import { reactive } from 'vue';

	// 代购信息 - 使用统一的 businessInfo 命名
	const businessInfo = reactive({
		productLink: '',
		description: '',
		count: 1,
		price: '',
		remark: ''
	});

	const increaseCount = () => {
		if (businessInfo.count < 10) {
			businessInfo.count++;
		}
	};

	const decreaseCount = () => {
		if (businessInfo.count > 1) {
			businessInfo.count--;
		}
	};

	// 暴露业务数据给父组件（统一接口）
	const getBusinessData = () => {
		return { ...businessInfo };
	};

	// 验证业务表单
	const validateForm = () => {
		if (!businessInfo.productLink) {
			uni.showToast({ title: '请输入商品链接', icon: 'none' });
			return false;
		}

		if (!businessInfo.description) {
			uni.showToast({ title: '请输入商品描述', icon: 'none' });
			return false;
		}

		if (!businessInfo.price) {
			uni.showToast({ title: '请输入商品价格', icon: 'none' });
			return false;
		}

		return true;
	};

	// 暴露方法给父组件（统一接口）
	defineExpose({
		getBusinessData,
		validateForm
	});
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
		width: 100%;
		font-size: 16px;
		height: 50rpx;
		line-height: 50rpx;
		border-bottom: 1px solid #eee;
		padding-bottom: 15rpx;
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
</style>
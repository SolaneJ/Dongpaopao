<template>
	<view class="card">
		<u-title>快递信息</u-title>
		<!-- 取件码输入 -->
		<view class="input-group">
			<text class="input-label">取件码/单号</text>
			<view class="input-wrapper">
				<input v-model="businessInfo.code" placeholder="请输入取件码或快递单号" class="input-field" />
				<uni-icons type="scan" size="20" color="#999" @click="handleScan" />
			</view>
		</view>
		<!-- 快递公司选择 -->
		<view class="input-group">
			<text class="input-label">快递公司</text>
			<picker mode="selector" :range="expressCompanies" @change="handleCompanyChange">
				<view class="picker">
					{{businessInfo.company || '请选择快递公司'}}
					<uni-icons type="arrowdown" size="16" color="#999" />
				</view>
			</picker>
		</view>
		<!-- 存放位置 -->
		<view class="input-group">
			<text class="input-label">存放位置</text>
			<picker mode="selector" :range="storageLocations" @change="handleLocationChange">
				<view class="picker">
					{{businessInfo.location || '请选择存放位置'}}
					<uni-icons type="arrowdown" size="16" color="#999" />
				</view>
			</picker>
		</view>
		<!-- 备注 -->
		<view class="input-group">
			<text class="input-label">备注信息</text>
			<textarea v-model="businessInfo.remark" placeholder="可填写特殊要求（选填）" class="textarea-field" />
		</view>
	</view>
</template>

<script lang="ts" setup>
	import { ref, reactive } from 'vue';

	// 业务信息（统一使用 businessInfo 命名）
	const businessInfo = reactive({
		code: '',
		company: '',
		location: '',
		remark: ''
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

	const handleScan = () => {
		uni.scanCode({
			success: (res) => {
				businessInfo.code = res.result;
			}
		});
	};

	const handleCompanyChange = (e : any) => {
		businessInfo.company = expressCompanies.value[e.detail.value];
	};

	const handleLocationChange = (e : any) => {
		businessInfo.location = storageLocations.value[e.detail.value];
	};

	// 暴露业务数据给父组件（统一接口）
	const getBusinessData = () => {
		return { ...businessInfo };
	};

	// 验证业务表单
	const validateForm = () => {
		if (!businessInfo.code) {
			uni.showToast({ title: '请输入取件码或单号', icon: 'none' });
			return false;
		}

		if (!businessInfo.company) {
			uni.showToast({ title: '请选择快递公司', icon: 'none' });
			return false;
		}

		if (!businessInfo.location) {
			uni.showToast({ title: '请选择存放位置', icon: 'none' });
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
</style>
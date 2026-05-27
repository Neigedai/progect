const BASE_URL = 'http://localhost:8080'

Page({
  data: {
    year: new Date().getFullYear(),
    code: '',
    loading: true,
    error: '',
    submitted: false,
    form: { companyName:'',industry:'',employeeCount:'',contactName:'',contactPhone:'',q1:null,q2:null,q3:null,q4:null,q5:null,q6:null,q7:null,q8:null,q9:null,q10:null,q11:'' },
    questions: [
      {
        section: '一、空间服务',
        items: [
          { key:'q1', label:'1、您对办公空间及环境的满意程度', options:[10,8,6,4,2] },
          { key:'q2', label:'2、您对会议室、水吧、书吧等公共场所的满意程度', options:[10,8,6,4,2] },
          { key:'q3', label:'3、您对物业服务人员能力的满意程度', options:[8,6,4,2] }
        ]
      },
      {
        section: '二、活动服务',
        items: [
          { key:'q4', label:'4、各类活动对自身企业是否有帮助', options:[10,8,6,4,2] },
          { key:'q5', label:'5、人员培训、创业指导等活动的满意程度', options:[8,6,4,2] },
          { key:'q6', label:'6、投融资对接服务的满意程度', options:[8,6,4,2] },
          { key:'q7', label:'7、信息服务、管理咨询等服务的满意程度', options:[8,6,4,2] }
        ]
      },
      {
        section: '三、服务质量',
        items: [
          { key:'q8', label:'8、服务计划及实施是否及时', options:[8,6,4,2] },
          { key:'q9', label:'9、服务质量与效果是否满足需求', options:[10,8,6,4,2] },
          { key:'q10', label:'10、临时和应急工作的反应能力', options:[10,8,6,4,2] }
        ]
      }
    ]
  },

  onLoad(options) {
    const code = options.code
    if (!code) { this.setData({ error:'链接无效', loading:false }); return }
    this.setData({ code })
    wx.request({
      url: BASE_URL + '/api/survey/' + code,
      success: res => {
        if (res.data.code !== 200) { this.setData({ error: res.data.message || '问卷不存在' }); return }
        if (res.data.data.status === 'completed') { this.setData({ error:'该问卷已提交' }); return }
        this.setData({ loading: false })
      },
      fail: () => this.setData({ error:'网络异常', loading: false })
    })
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field
    this.setData({ ['form.' + field]: e.detail.value })
  },

  onSelect(e) {
    const { key, val } = e.currentTarget.dataset
    this.setData({ ['form.' + key]: val })
  },

  handleSubmit() {
    wx.request({
      url: BASE_URL + '/api/survey/' + this.data.code + '/submit',
      method: 'POST',
      data: this.data.form,
      success: res => {
        if (res.data.code === 200) { this.setData({ submitted: true }) }
        else { wx.showToast({ title: res.data.message, icon: 'none' }) }
      },
      fail: () => wx.showToast({ title:'提交失败', icon:'none' })
    })
  }
})

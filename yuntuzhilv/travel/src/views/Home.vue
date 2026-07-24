<template>
  <div class="home-shell">
    <header class="topbar">
      <div class="brand-block">
        <div class="brand">WayCloud 云途智行</div>
      </div>

      <div class="user-area" v-if="displayName">
        <button class="user-chip" @click="toggleMenu">
          <span class="avatar">{{ avatarInitial }}</span>
          <span class="name">{{ displayName }}</span>
          <span class="caret">▾</span>
        </button>
        <div v-if="menuOpen" class="dropdown">
          <button class="mytrip" @click="router.push('/my-itinerary')">我的行程</button>
          <button class="user-center" @click="router.push('/user-center')">个人中心</button>
          <button class="logout" @click="logout">退出登录</button>
        </div>
      </div>
    </header>

    <main class="content">
      <section class="banner">
        <div class="banner-copy">
          <h2>云途智行・AI 智能行程规划平台</h2>
          <p>依托偏好智能匹配景点、酒店与交通，一键生成专属旅行方案</p>
          <button class="primary-btn" @click="router.push('/create-itinerary')">进入行程定制向导</button>
        </div>
        <div class="banner-buttons">
          <button class="banner-btn" @click="router.push('/attractions')">
            <span class="btn-icon">🔍</span>
            <span class="btn-text">景点浏览</span>
          </button>
          <button class="banner-btn" @click="router.push('/hotels')">
            <span class="btn-icon">🏨</span>
            <span class="btn-text">酒店查询</span>
          </button>
          <button class="banner-btn" @click="router.push('/traffic')">
            <span class="btn-icon">🚄</span>
            <span class="btn-text">交通查询</span>
          </button>
        </div>
      </section>

      <section class="hot-attractions">
        <div class="section-header">
          <h2>热门推荐</h2>
          <button class="view-more-btn" @click="router.push('/attractions')">查看更多</button>
        </div>

        <div class="search-bar">
          <input type="text" v-model="searchKeyword" placeholder="搜索目的地、景点..." class="search-input" />
          <button class="search-btn" @click="handleSearch">搜索</button>
        </div>

        <div class="category-tags">
          <button v-for="tag in categoryTags" :key="tag" :class="['tag-btn', { active: selectedTag === tag }]"
            @click="selectTag(tag)">
            {{ tag }}
          </button>
        </div>

        <div class="attractions-grid">
          <article class="attraction-card" v-for="item in paginatedAttractions" :key="item.name"
            @click="showAttractionDetail(item)">
            <div class="card-image">
              <img :src="item.image" :alt="item.name" />
              <div class="card-overlay">
                <p class="overlay-description">{{ item.description }}</p>
              </div>
            </div>
            <div class="card-info">
              <h3 class="card-title">{{ item.name }}</h3>
              <p class="card-meta">
                <span class="card-type">{{ item.type }}</span>
                <span class="card-divider">|</span>
                <span class="card-address">{{ item.address }}</span>
              </p>
              <div class="card-footer">
                <span class="card-price">门票: {{ item.price }}</span>
                <div class="card-rating">
                  <span class="star">★</span>
                  <span class="rating-value">{{ item.rating }}</span>
                </div>
              </div>
            </div>
          </article>
        </div>

        <nav class="pagination-nav" v-if="totalPages > 1">
          <button class="page-btn" :disabled="currentPage <= 1" @click="goToPage(currentPage - 1)">‹ 上一页</button>
          <button v-for="p in pageNumbers" :key="p" :class="['page-btn', 'page-num', { active: p === currentPage }]"
            @click="goToPage(p)">{{ p }}</button>
          <button class="page-btn" :disabled="currentPage >= totalPages" @click="goToPage(currentPage + 1)">下一页
            ›</button>
        </nav>
      </section>
    </main>

    <div v-if="showDetailModal" class="modal-overlay" @click.self="closeDetailModal">
      <div class="detail-modal">
        <button class="modal-close" @click="closeDetailModal">&times;</button>
        <div class="modal-content-inner">
          <h3>{{ selectedAttraction?.name }}</h3>
          <p><strong>类型：</strong>{{ selectedAttraction?.type }}</p>
          <p><strong>地址：</strong>{{ selectedAttraction?.address }}</p>
          <p><strong>评分：</strong>{{ selectedAttraction?.rating }}</p>
          <p><strong>门票：</strong>{{ selectedAttraction?.price }}</p>
          <p><strong>简介：</strong>{{ selectedAttraction?.description }}</p>
        </div>
        <div class="modal-actions">
          <button class="modal-want-go-btn" @click="goToItinerary">
            <span class="btn-pin-icon">📍</span>想去这里
          </button>
          <button class="modal-confirm-btn" @click="closeDetailModal">确定</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { getHotAttractionsApi } from '../api/stats'

const router = useRouter()
const displayName = ref('')
const menuOpen = ref(false)
const searchKeyword = ref('')
const selectedTag = ref('')
const showDetailModal = ref(false)
const selectedAttraction = ref(null)
const attractions = ref([])

const categoryTags = ['历史古迹', '自然风光', '美食', '购物', '北京', '上海', '武汉', '杭州', '成都', '西安']

const mockAttractions = [
  {
    name: '故宫博物院',
    type: '历史古迹',
    address: '北京市东城区景山前街4号',
    price: '¥60',
    rating: '4.9',
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Beijing%20Forbidden%20City%20Palace%20Museum%2C%20traditional%20Chinese%20architecture%2C%20golden%20roof%2C%20red%20walls%2C%20high%20quality%20photo&image_size=landscape_4_3',
    description: '世界上现存规模最大、保存最为完整的木质结构古建筑群，是明清两代的皇家宫殿。'
  },
  {
    name: '八达岭长城',
    type: '历史古迹',
    address: '北京市延庆区军都山关沟古道北口',
    price: '¥40',
    rating: '4.8',
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Great%20Wall%20of%20China%20Badaling%2C%20mountain%20landscape%2C%20ancient%20fortress%2C%20high%20quality%20photo&image_size=landscape_4_3',
    description: '万里长城的重要组成部分，是明长城的一个隘口，以其宏伟的景观和深厚的历史文化底蕴闻名于世。'
  },
  {
    name: '颐和园',
    type: '历史古迹',
    address: '北京市海淀区新建宫门路19号',
    price: '¥30',
    rating: '4.8',
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Summer%20Palace%20Beijing%2C%20beautiful%20lake%2C%20traditional%20pavilion%2C%20willow%20trees%2C%20high%20quality%20photo&image_size=landscape_4_3',
    description: '中国现存规模最大、保存最完整的皇家园林，被誉为"皇家园林博物馆"。'
  },
  {
    name: '天坛公园',
    type: '历史古迹',
    address: '北京市东城区天坛东里甲1号',
    price: '¥15',
    rating: '4.7',
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Temple%20of%20Heaven%20Beijing%2C%20blue%20roof%2C%20ancient%20Chinese%20temple%2C%20beautiful%20sky%2C%20high%20quality%20photo&image_size=landscape_4_3',
    description: '明清两代皇帝"祭天""祈谷"的场所，是中国现存最大的古代祭祀建筑群。'
  },
  {
    name: '圆明园',
    type: '历史古迹',
    address: '北京市海淀区清华西路28号',
    price: '¥25',
    rating: '4.6',
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Old%20Summer%20Palace%20Yuanmingyuan%20Beijing%2C%20ancient%20ruins%2C%20beautiful%20garden%2C%20lake%2C%20high%20quality%20photo&image_size=landscape_4_3',
    description: '清代大型皇家园林，被誉为"万园之园"，曾是中国古代园林艺术的巅峰之作。'
  },
  {
    name: '鸟巢',
    type: '自然风光',
    address: '北京市朝阳区国家体育场南路1号',
    price: '¥50',
    rating: '4.5',
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Beijing%20National%20Stadium%20Birds%20Nest%2C%20modern%20architecture%2C%20steel%20structure%2C%20high%20quality%20photo&image_size=landscape_4_3',
    description: '2008年北京奥运会主体育场，以其独特的鸟巢造型成为北京的地标性建筑。'
  },
  {
    name: '水立方',
    type: '自然风光',
    address: '北京市朝阳区天辰东路11号',
    price: '¥30',
    rating: '4.4',
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Water%20Cube%20Beijing%20National%20Aquatics%20Center%2C%20blue%20bubbles%20architecture%2C%20modern%20building%2C%20high%20quality%20photo&image_size=landscape_4_3',
    description: '2008年北京奥运会游泳比赛场馆，以其梦幻的蓝色泡泡外观闻名。'
  },
  {
    name: '香山公园',
    type: '自然风光',
    address: '北京市海淀区买卖街40号',
    price: '¥10',
    rating: '4.5',
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Fragrant%20Hill%20Park%20Beijing%2C%20beautiful%20mountains%2C%20red%20leaves%20autumn%2C%20forest%2C%20high%20quality%20photo&image_size=landscape_4_3',
    description: '北京著名的森林公园，以秋季红叶和清幽的自然风光著称。'
  },
  {
    name: '什刹海',
    type: '自然风光',
    address: '北京市西城区地安门西大街',
    price: '¥0',
    rating: '4.6',
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Shichahai%20Beijing%2C%20beautiful%20lake%2C%20traditional%20architecture%2C%20night%20view%2C%20high%20quality%20photo&image_size=landscape_4_3',
    description: '北京城内面积最大、风貌保存最完整的一片历史街区，拥有优美的湖泊风光。'
  },
  {
    name: '东方明珠塔',
    type: '自然风光',
    address: '上海市浦东新区陆家嘴世纪大道1号',
    price: '¥199',
    rating: '4.7',
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Oriental%20Pearl%20Tower%20Shanghai%2C%20city%20skyline%2C%20modern%20skyscraper%2C%20night%20view%2C%20high%20quality%20photo&image_size=landscape_4_3',
    description: '上海的标志性文化景观之一，是亚洲第一、世界第三高塔。'
  },
  {
    name: '外滩',
    type: '自然风光',
    address: '上海市黄浦区中山东一路',
    price: '¥0',
    rating: '4.8',
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Shanghai%20Bund%2C%20historical%20buildings%2C%20city%20skyline%2C%20Huangpu%20River%2C%20high%20quality%20photo&image_size=landscape_4_3',
    description: '上海最具代表性的景观之一，拥有众多历史建筑和壮丽的城市天际线。'
  },
  {
    name: '上海迪士尼乐园',
    type: '购物',
    address: '上海市浦东新区川沙新镇',
    price: '¥599',
    rating: '4.7',
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Shanghai%20Disneyland%20Castle%2C%20theme%20park%2C%20fantasy%20castle%2C%20beautiful%20colors%2C%20high%20quality%20photo&image_size=landscape_4_3',
    description: '中国大陆首座迪士尼主题乐园，拥有七大主题园区和众多精彩游乐项目。'
  },
  {
    name: '北京王府井小吃街',
    type: '美食',
    address: '北京市东城区王府井大街',
    price: '¥50',
    rating: '4.6',
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Beijing%20Wangfujing%20Snack%20Street%2C%20traditional%20Chinese%20food%20stalls%2C%20night%20market%2C%20busy%20street%2C%20high%20quality%20photo&image_size=landscape_4_3',
    description: '北京最著名的小吃街之一，汇集了全国各地的特色美食，是体验老北京风味的好去处。'
  },
  {
    name: '南锣鼓巷',
    type: '美食',
    address: '北京市东城区南锣鼓巷',
    price: '¥0',
    rating: '4.7',
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Beijing%20Nanluoguxiang%20Alley%2C%20traditional%20hutong%2C%20cafe%20shops%2C%20traditional%20architecture%2C%20high%20quality%20photo&image_size=landscape_4_3',
    description: '北京保存最完整的胡同街区之一，拥有众多特色小吃店和创意咖啡馆，充满文艺气息。'
  },
  {
    name: '上海城隍庙',
    type: '美食',
    address: '上海市黄浦区豫园路100号',
    price: '¥0',
    rating: '4.8',
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Shanghai%20City%20God%20Temple%20Chenghuangmiao%2C%20traditional%20temple%2C%20colorful%20lanterns%2C%20busy%20market%2C%20high%20quality%20photo&image_size=landscape_4_3',
    description: '上海著名的旅游景点，汇集了上海特色小吃，如南翔小笼、蟹壳黄等，是品尝上海美食的必去之地。'
  },
  {
    name: '外滩美食街',
    type: '美食',
    address: '上海市黄浦区中山东一路',
    price: '¥100',
    rating: '4.5',
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Shanghai%20Bund%20food%20street%2C%20fine%20dining%2C%20restaurants%2C%20city%20skyline%20view%2C%20night%20view%2C%20high%20quality%20photo&image_size=landscape_4_3',
    description: '外滩周边汇集了众多高端餐厅和特色美食，可一边品尝美食一边欣赏黄浦江两岸的美景。'
  },
  {
    name: '北京簋街',
    type: '美食',
    address: '北京市东城区东直门内大街',
    price: '¥80',
    rating: '4.5',
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Beijing%20Guijie%20Street%2C%20spicy%20food%20restaurants%2C%20hot%20pot%2C%20night%20life%2C%20red%20lanterns%2C%20high%20quality%20photo&image_size=landscape_4_3',
    description: '北京最著名的美食街之一，以麻辣小龙虾和各类川菜闻名，夜晚灯火辉煌，热闹非凡。'
  },
  {
    name: '七宝老街',
    type: '美食',
    address: '上海市闵行区七宝镇',
    price: '¥0',
    rating: '4.6',
    image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Shanghai%20Qibao%20Ancient%20Town%2C%20traditional%20water%20town%2C%20snack%20stalls%2C%20old%20streets%2C%20high%20quality%20photo&image_size=landscape_4_3',
    description: '上海著名的古镇，拥有丰富的江南特色小吃，如七宝汤团、方糕、羊肉等，充满江南水乡韵味。'
  },
  {
    name: '黄鹤楼',
    type: '历史古迹',
    address: '武汉市武昌区蛇山',
    price: '¥70',
    rating: '4.8',
    image: 'http://q6.itc.cn/images01/20250924/ff2de109c59449a980f7bb187142b289.jpeg',
    description: '江南三大名楼之一，因崔颢诗"黄鹤一去不复返，白云千载空悠悠"而闻名天下。'
  },
  {
    name: '东湖风景区',
    type: '自然风光',
    address: '武汉市武昌区东湖路',
    price: '¥0',
    rating: '4.7',
    image: 'http://gips2.baidu.com/it/u=2990829944,2898902058&fm=3074&app=3074&f=JPEG?w=1440&h=1050&type=normal&func=',
    description: '中国最大的城中湖，水域面积是杭州西湖的6倍，以湖光山色和楚文化闻名。'
  },
  {
    name: '武汉大学',
    type: '自然风光',
    address: '武汉市武昌区珞珈山',
    price: '¥0',
    rating: '4.9',
    image: 'http://q1.itc.cn/images01/20250316/3fabb3a1aac2478fb6876ba85a9870f5.jpeg',
    description: '中国最美的大学之一，以樱花和民国建筑闻名，是赏樱的绝佳去处。'
  },
  {
    name: '户部巷',
    type: '美食',
    address: '武汉市武昌区户部巷',
    price: '¥30',
    rating: '4.6',
    image: 'http://pic.rmb.bdstatic.com/bjh/240722/beautify/a21a6de3be26999739cf484e1c4d84b9.jpeg@c_1,w_1440,h_1080,x_0,y_0',
    description: '武汉最著名的美食街，汇集了热干面、豆皮、三鲜豆皮等武汉特色小吃。'
  },
  {
    name: '西湖',
    type: '自然风光',
    address: '杭州市西湖区龙井路1号',
    price: '¥0',
    rating: '4.9',
    image: 'http://q4.itc.cn/q_70/images01/20250929/d8d6541ecd804e05ad546bf3e66bb48d.jpeg',
    description: '中国最著名的湖泊之一，以"欲把西湖比西子，淡妆浓抹总相宜"闻名，是世界文化遗产。'
  },
  {
    name: '灵隐寺',
    type: '历史古迹',
    address: '杭州市西湖区灵隐路',
    price: '¥75',
    rating: '4.8',
    image: 'http://miaobi-lite.bj.bcebos.com/miaobi/5mao/b%27MjAyNOeBtemakOWvuumXqOelqOS7t%2BagvF8xNzI4NzgwODU1LjM2OTU1NzY%3D%27/0.png',
    description: '江南著名古刹，始建于东晋，是杭州最著名的佛教寺院和旅游景点。'
  },
  {
    name: '宋城',
    type: '购物',
    address: '杭州市西湖区之江路148号',
    price: '¥290',
    rating: '4.7',
    image: 'http://miaobi-lite.bj.bcebos.com/miaobi/5mao/b%27LV8xNzMxNDg1MjQ3LjkzNDkyNw%3D%3D%27/0.png?authorization=bce-auth-v1%2FALTAKmda7zOvhZVbRzBLewvCMU%2F2024-11-13T08%3A07%3A29Z%2F-1%2F%2Ffa49ff07028b57ebcec40af536760afc40f8da9da572193e16784aa6bd8e7e4c',
    description: '大型宋文化主题公园，以"给我一天，还你千年"为口号，提供沉浸式宋代文化体验。'
  },
  {
    name: '河坊街',
    type: '美食',
    address: '杭州市上城区河坊街',
    price: '¥0',
    rating: '4.6',
    image: 'http://q9.itc.cn/images01/20260127/a503be29bafe4b2c85a7cf78c087d4f2.jpeg',
    description: '杭州最著名的历史文化街区，汇集了各种传统小吃和手工艺品店。'
  },
  {
    name: '武侯祠',
    type: '历史古迹',
    address: '成都市武侯区武侯祠大街231号',
    price: '¥50',
    rating: '4.8',
    image: 'http://miaobi-lite.bj.bcebos.com/miaobi/5mao/b%275oiQ6YO957qi5aKZ5pmv54K55Zyo5ZOq6YeMXzE3MzIwODM5NzMuNDk0NDU4Ml8xNzMyMDgzOTc0LjQ3MzQxNl8xNzMyMDgzOTc0Ljg2ODQ3NjQ%3D%27/2.png',
    description: '中国唯一的君臣合祀祠庙，纪念刘备和诸葛亮，是三国文化的重要遗址。'
  },
  {
    name: '都江堰',
    type: '历史古迹',
    address: '成都市都江堰市公园路',
    price: '¥80',
    rating: '4.9',
    image: 'http://miaobi-lite.bj.bcebos.com/miaobi/5mao/b%276YO95rGf5aCw6aOe5rKZ5aCw55qE5bel5L2c5Y6f55CGXzE3MzAwMDM0ODkuNjI2MTc2NA%3D%3D%27/0.png',
    description: '世界文化遗产，公元前256年修建的水利工程，至今仍在使用，是古代智慧的结晶。'
  },
  {
    name: '成都大熊猫繁育研究基地',
    type: '自然风光',
    address: '成都市成华区熊猫大道1375号',
    price: '¥55',
    rating: '4.9',
    image: 'http://gips1.baidu.com/it/u=3384835029,235844858&fm=3074&app=3074&f=JPEG',
    description: '世界最大的大熊猫人工繁育基地，可以近距离观赏可爱的大熊猫。'
  },
  {
    name: '锦里古街',
    type: '美食',
    address: '成都市武侯区锦里',
    price: '¥0',
    rating: '4.7',
    image: 'http://b0.bdstatic.com/ugc/DEtj2QMYpZV9XO3gyAV3pA807f6126b16c140bb8d0a35106fcc32f.jpg',
    description: '成都最著名的美食街，汇集了川菜、火锅、小吃等各种成都特色美食。'
  },
  {
    name: '兵马俑',
    type: '历史古迹',
    address: '西安市临潼区秦陵北路',
    price: '¥120',
    rating: '4.9',
    image: 'http://n.sinaimg.cn/spider20250928/214/w2048h1366/20250928/1303-aa4d1a31ab2583ae0da06374d3823a48.jpg',
    description: '世界第八大奇迹，秦始皇陵的陪葬坑，展示了秦代军事阵容的壮观景象。'
  },
  {
    name: '大雁塔',
    type: '历史古迹',
    address: '西安市雁塔区慈恩路1号',
    price: '¥40',
    rating: '4.8',
    image: 'http://gips0.baidu.com/it/u=2342055667,444217554&fm=3074&app=3074&f=JPEG',
    description: '唐代著名佛塔，玄奘法师译经藏经之所，是西安的标志性建筑。'
  },
  {
    name: '华清宫',
    type: '历史古迹',
    address: '西安市临潼区华清路38号',
    price: '¥120',
    rating: '4.7',
    image: 'http://gips0.baidu.com/it/u=2887051880,212817742&fm=3074&app=3074&f=JPEG',
    description: '唐代皇家温泉行宫，因唐明皇与杨贵妃的爱情故事而闻名，是著名的温泉浴场。'
  },
  {
    name: '回民街',
    type: '美食',
    address: '西安市莲湖区北院门',
    price: '¥50',
    rating: '4.6',
    image: 'http://gips2.baidu.com/it/u=3951914452,2180082284&fm=3074&app=3074&f=JPEG?w=1440&h=1050&type=normal&func=',
    description: '西安最著名的美食街，汇集了各种回族特色美食，如羊肉泡馍、肉夹馍、凉皮等。'
  }
]

const displayAttractions = computed(() => {
  return attractions.value.length > 0 ? attractions.value : mockAttractions
})

const filteredAttractions = computed(() => {
  return displayAttractions.value.filter(item => {
    const matchKeyword = !searchKeyword.value ||
      item.name.includes(searchKeyword.value) ||
      item.address.includes(searchKeyword.value)
    const matchTag = !selectedTag.value ||
      item.type === selectedTag.value ||
      item.address.includes(selectedTag.value)
    return matchKeyword && matchTag
  })
})

const currentPage = ref(1)
const pageSize = 9

const totalPages = computed(() => Math.ceil(filteredAttractions.value.length / pageSize) || 1)

const paginatedAttractions = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredAttractions.value.slice(start, start + pageSize)
})

watch([searchKeyword, selectedTag], () => {
  currentPage.value = 1
})

const pageNumbers = computed(() => {
  const pages = []
  const total = totalPages.value
  const current = currentPage.value
  let start = Math.max(1, current - 2)
  let end = Math.min(total, start + 4)
  if (end - start < 4) start = Math.max(1, end - 4)
  for (let i = start; i <= end; i++) pages.push(i)
  return pages
})

function goToPage(page) {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
    document.querySelector('.hot-attractions')?.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }
}

async function loadHotAttractions() {
  try {
    const res = await getHotAttractionsApi()
    const data = res?.data?.list || res?.list || []
    if (data.length > 0) {
      attractions.value = data.map(item => ({
        name: item.name,
        type: item.type || '其他',
        address: item.address || '',
        price: item.price || '免费',
        rating: item.rating || '4.0',
        image: item.image || `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=${encodeURIComponent(item.name + ', beautiful scenery, high quality photo')}&image_size=square_hd`,
        description: item.description || item.intro || ''
      }))
    }
  } catch (e) {
    attractions.value = []
  }
}

const avatarInitial = computed(() => (displayName.value ? displayName.value.charAt(0).toUpperCase() : 'U'))

function readSession() {
  try {
    displayName.value = localStorage.getItem('username') || ''
  } catch (e) {
    displayName.value = ''
  }
}

function closeMenu(event) {
  if (!event.target.closest('.user-area')) {
    menuOpen.value = false
  }
}

function toggleMenu() {
  menuOpen.value = !menuOpen.value
}

function selectTag(tag) {
  selectedTag.value = selectedTag.value === tag ? '' : tag
}

function showAttractionDetail(item) {
  selectedAttraction.value = item
  showDetailModal.value = true
}

function closeDetailModal() {
  showDetailModal.value = false
  selectedAttraction.value = null
}

/** 从地址中提取城市名，跳转行程定制页并自动填充目的地 */
function goToItinerary() {
  const address = selectedAttraction.value?.address || ''
  const match = address.match(/^(.+?市)/)
  const city = match ? match[1].replace('市', '') : address.slice(0, 2)
  showDetailModal.value = false
  selectedAttraction.value = null
  window.scrollTo(0, 0)
  router.push({ path: '/create-itinerary', query: { toCity: city } })
}

function handleSearch() {
}

function logout() {
  try {
    localStorage.removeItem('token')
    localStorage.removeItem('role')
    localStorage.removeItem('userId')
    localStorage.removeItem('username')
  } catch (e) {
    // noop
  }
  router.replace('/BLhome')
}

onMounted(() => {
  readSession()
  document.addEventListener('click', closeMenu)
  loadHotAttractions()
})

onBeforeUnmount(() => {
  document.removeEventListener('click', closeMenu)
})
</script>

<style scoped>
.home-shell {
  min-height: 100vh;
  background: radial-gradient(circle at top left, #d8f2e8 0, #f1fbf6 40%, #f7fcfa 100%);
  color: #16382f;
}

.topbar {
  max-width: 1280px;
  margin: 0 auto;
  padding: 18px 24px 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.brand {
  font-size: 20px;
  font-weight: 800;
  color: #0f5132;
}



.user-area {
  position: relative;
}

.user-chip {
  border: none;
  display: flex;
  align-items: center;
  gap: 10px;
  border-radius: 999px;
  padding: 8px 12px;
  background: #ffffff;
  box-shadow: 0 8px 20px rgba(15, 81, 50, 0.08);
  cursor: pointer;
}

.avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: linear-gradient(135deg, #2d8a6e, #1a5a45);
  color: #fff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
}

.caret {
  color: #6f8279;
}

.dropdown {
  position: absolute;
  right: 0;
  top: calc(100% + 8px);
  min-width: 160px;
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 16px 32px rgba(15, 81, 50, 0.14);
  overflow: hidden;
  z-index: 20;
}

.dropdown button {
  width: 100%;
  padding: 12px 14px;
  border: none;
  background: #fff;
  text-align: left;
  cursor: pointer;
  transition: all 0.3s ease;
}

.dropdown .mytrip:hover,
.dropdown .user-center:hover {
  background: #f1dddf;
}

.dropdown .logout:hover {
  background: #e72d48;

}



.content {
  max-width: 1280px;
  margin: 0 auto;
  padding: 12px 24px 40px;
}

.banner {
  min-height: 300px;
  border-radius: 20px;
  padding: 40px;
  display: grid;
  grid-template-columns: minmax(0, 1.2fr) minmax(200px, 0.8fr);
  gap: 32px;
  align-items: center;
  background: linear-gradient(135deg, #1a5a45 0%, #0f5132 50%, #1a5a45 100%);
  color: #fff;
  box-shadow: 0 12px 32px rgba(15, 81, 50, 0.3);
}

.banner-copy h2 {
  font-size: clamp(24px, 4vw, 40px);
  line-height: 1.2;
  margin: 0 0 16px;
  font-weight: 800;
}

.banner-copy p {
  max-width: 40rem;
  line-height: 1.75;
  color: rgba(255, 255, 255, 0.9);
  font-size: 16px;
  margin: 0 0 24px;
}

.primary-btn {
  border: none;
  border-radius: 999px;
  cursor: pointer;
  margin-top: 0;
  padding: 12px 24px;
  background: #f8f2d9;
  color: #184d3b;
  font-weight: 700;
  font-size: 14px;
  transition: all 0.3s;
}

.primary-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(248, 242, 217, 0.3);
}

.banner-buttons {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.banner-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  padding: 20px 32px;
  border: none;
  border-radius: 16px;
  background: linear-gradient(90deg, #b8e6d2 0%, #51b891 100%);
  color: #0f5132;
  font-size: 22px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 12px rgba(81, 184, 145, 0.3);
}

.banner-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(81, 184, 145, 0.4);
}

.btn-icon {
  font-size: 28px;
}

.btn-text {
  letter-spacing: 2px;
}

.hot-attractions {
  margin-top: 24px;
}

.section-header {
  margin-bottom: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-header h2 {
  margin: 0;
  color: #0f5132;
  font-size: 24px;
  font-weight: 700;
  border-bottom: 2px solid #2d8a6e;
  padding-bottom: 8px;
}

.view-more-btn {
  padding: 8px 16px;
  background: #e9f6ef;
  color: #1a5a45;
  border: none;
  border-radius: 999px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.view-more-btn:hover {
  background: #2d8a6e;
  color: white;
}

.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.search-input {
  flex: 1;
  padding: 12px 16px;
  border: 2px solid #e5e5e5;
  border-radius: 8px;
  font-size: 16px;
  outline: none;
  transition: all 0.3s;
}

.search-input:focus {
  border-color: #2d8a6e;
  box-shadow: 0 0 0 3px rgba(45, 138, 110, 0.1);
}

.search-btn {
  padding: 12px 24px;
  background: linear-gradient(135deg, #6b72ff 0%, #4338ca 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.search-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(107, 114, 255, 0.3);
}

.category-tags {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.tag-btn {
  padding: 8px 16px;
  background: #fff;
  border: none;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
  color: #666;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.tag-btn:hover {
  background: #e9f6ef;
  color: #1a5a45;
}

.tag-btn.active {
  background: linear-gradient(135deg, #f0c76d, #d7942b);
  color: #27321f;
  font-weight: 600;
}

.attractions-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 20px;
}

.attraction-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.3s ease;
}

.attraction-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
}

.card-image {
  width: 100%;
  height: 180px;
  overflow: hidden;
  position: relative;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.card-info {
  padding: 12px;
}

.card-title {
  margin: 0 0 8px;
  font-size: 18px;
  font-weight: 700;
  color: #333;
}

.card-meta {
  margin: 0 0 8px;
  font-size: 13px;
  color: #666;
  display: flex;
  align-items: center;
  gap: 6px;
}

.card-type {
  background: #fef3c7;
  color: #92400e;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.card-divider {
  color: #ccc;
}

.card-address {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-price {
  font-size: 14px;
  color: #dc2626;
  font-weight: 600;
}

.card-rating {
  display: flex;
  align-items: center;
  gap: 4px;
}

.star {
  color: #f0c76d;
  font-size: 14px;
}

.rating-value {
  font-size: 14px;
  color: #333;
  font-weight: 600;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.3s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }

  to {
    opacity: 1;
  }
}

.detail-modal {
  background: white;
  border-radius: 16px;
  padding: 32px;
  width: 100%;
  max-width: 480px;
  position: relative;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: slideUp 0.3s ease-out;
}

@keyframes slideUp {
  from {
    transform: translateY(30px);
    opacity: 0;
  }

  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.modal-close {
  position: absolute;
  top: 16px;
  right: 16px;
  background: none;
  border: none;
  font-size: 28px;
  color: #999;
  cursor: pointer;
  transition: color 0.3s;
  line-height: 1;
}

.modal-close:hover {
  color: #2d8a6e;
}

.modal-content-inner {
  margin-bottom: 24px;
}

.modal-content-inner h3 {
  margin: 0 0 20px;
  font-size: 24px;
  font-weight: 700;
  color: #0f5132;
}

.modal-content-inner p {
  margin: 12px 0;
  font-size: 15px;
  line-height: 1.8;
  color: #333;
}

.modal-content-inner strong {
  color: #2d8a6e;
}

.modal-actions {
  display: flex;
  gap: 12px;
}

.modal-want-go-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 14px;
  background: linear-gradient(135deg, #2d8a6e 0%, #1a5a45 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.modal-want-go-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(45, 138, 110, 0.4);
}

.btn-pin-icon {
  font-size: 18px;
}

.modal-confirm-btn {
  flex: 1;
  padding: 14px;
  background: linear-gradient(135deg, #6b72ff 0%, #4338ca 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.modal-confirm-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(107, 114, 255, 0.4);
}

.pagination-nav {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  margin-top: 32px;
  padding: 16px 0;
}

.page-btn {
  padding: 8px 14px;
  border: 1px solid #d1e8dd;
  border-radius: 8px;
  background: #fff;
  color: #1a5a45;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.25s;
}

.page-btn:hover:not(:disabled):not(.active) {
  background: #e9f6ef;
  border-color: #2d8a6e;
}

.page-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.page-btn.active {
  background: linear-gradient(135deg, #2d8a6e, #1a5a45);
  color: #fff;
  border-color: #1a5a45;
  font-weight: 600;
}

@media (max-width: 980px) {
  .banner {
    grid-template-columns: 1fr;
  }

  .attractions-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {

  .topbar,
  .content {
    padding-left: 16px;
    padding-right: 16px;
  }

  .banner {
    padding: 20px;
  }

  .attractions-grid {
    grid-template-columns: 1fr;
  }
}
</style>

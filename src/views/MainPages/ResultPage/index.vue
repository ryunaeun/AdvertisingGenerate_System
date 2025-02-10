<template>
  <div class="container py-7 preview-container">
    <Header />

    <div class="content-layout">
      <div class="left-column">
        <!-- 광고 노출 방법 모델 영역 -->
        <div class="text-center mb-5" v-if="recommendationData">
          <p style="color: #344767; font-size: 1.25rem; font-weight: bold; margin-bottom: 3px;">
            최적 노출 시간대는 {{ recommendationData.recommendations.time }}로 도출되었습니다.
          </p>
          <p style="color: #344767; font-size: 1.25rem; font-weight: bold; margin-bottom: 3px;">
            효과 극대화를 위해 {{ recommendationData.recommendations.adtype }}를 추천드립니다.
          </p>
        </div>
        <div class="text-center mb-5" v-else>
          <p style="color: #344767; font-size: 1.25rem; font-weight: bold; margin-bottom: 3px;">
            최적 노출 시간대는 20:00로 도출되었습니다.
          </p>
          <p style="color: #344767; font-size: 1.25rem; font-weight: bold; margin-bottom: 3px;">
            효과 극대화를 위해 배너 광고를 추천드립니다.
          </p>
        </div>

        <!-- 비디오 출력 영역 -->
        <div class="video-container">
          <div class="videos-grid single" id="videosGrid">
            <div class="video-cell">
              <div class="loading" id="loading-1" v-show="loadingVideo"></div>
              <video id="outputVideo-1" class="output-video" v-show="videoUrl" controls autoplay loop muted>
                <source :src="videoUrl" type="video/mp4">
                Your browser does not support the video tag.
              </video>
              <a id="downloadLink-1" class="download-link" v-show="videoUrl" :href="videoUrl" download
                 @click="downloadVideo">Download Video</a>
            </div>
          </div>
          <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
        </div>
      </div>

      <div class="right-column">
        <!-- 광고 키워드 그래프 영역 -->
        <div class="analysis-section">
          <div class="graph-container mb-3">
            <h3 class="graph-title">관련 키워드 분석</h3>
            <div class="input-group">
              <input type="text" placeholder="메인 키워드 입력" v-model="companyName" class="form-control" />
              <button class="btn btn-primary" @click="generateGraph">
                그래프 생성
              </button>
            </div>
            <div class="graph-wrapper">
              <svg id="graphSvg" width="100%" height="100%"></svg>
            </div>
          </div>

          <div class="result-container">
            <h3 class="result-title">분석 결과</h3>
            <p v-if="analysisResult">{{ analysisResult }}</p>
            <p v-else class="text-muted">분석 결과가 없습니다.</p>
          </div>
        </div>
      </div>
    </div>

    <!-- PathSettings 컴포넌트 (숨김 처리) -->
    <div style="display: none;">
      <PathSettings @path-change="handlePathChange" ref="pathSettings" />
    </div>
  </div>
</template>

<script>
import Header from "../HomePage/components/Header.vue";
import PathSettings from './components/PathSettings.vue';
import * as d3 from "d3";

export default {
  name: "ResultPage",
  components: {
    Header,
    PathSettings
  },
  data() {
    return {
      videoUrl: '',
      loadingVideo: true,
      errorMessage: '',
      serverUrl: 'http://125.181.20.252:8888',
      savePath: {
        userId: 'KTaivle',
        subPath: 'videos',
        fullPath: 'KTaivle\\videos'
      },
      recommendationData: null,
      companyName: "",
      analysisResult: "",
      nodes: [],
      links: [],
    }
  },
  methods: {
    async handlePathChange(pathData) {
      this.savePath = pathData;
      await this.loadLatestRecommendation();
      await this.loadLatestVideo();
    },

    async loadLatestRecommendation() {
      try {
        // 최신 추천 정보 가져오기
        const response = await fetch(`${this.serverUrl}/get_latest_recommendation?userId=${this.savePath.userId}`);

        if (!response.ok) {
          throw new Error('Failed to load recommendation');
        }

        const data = await response.json();
        if (data.success && data.recommendation) {
          this.recommendationData = data.recommendation;
        }
      } catch (error) {
        console.error('Error loading recommendation:', error);
        this.errorMessage = `Error loading recommendation: ${error.message}`;
      }
    },

    async loadLatestVideo() {
      this.loadingVideo = true;
      this.errorMessage = '';

      try {
        // 최신 비디오 정보 가져오기
        const response = await fetch(`${this.serverUrl}/get_latest_video?userId=${this.savePath.userId}`);

        if (!response.ok) {
          throw new Error('Failed to load video');
        }

        const data = await response.json();
        if (!data.success) {
          throw new Error(data.error || 'Video loading failed');
        }

        this.videoUrl = `${this.serverUrl}/output/${this.savePath.userId}/${data.folder}/${data.filename}`;
        await this.waitForVideoLoad(this.videoUrl);

      } catch (error) {
        console.error('Error loading video:', error);
        this.errorMessage = `Error loading video: ${error.message}`;
      } finally {
        this.loadingVideo = false;
      }
    },

    async waitForVideoLoad(videoUrl) {
      return new Promise((resolve, reject) => {
        const video = document.getElementById('outputVideo-1');
        if (!video) {
          reject(new Error('Video element not found'));
          return;
        }

        video.onloadeddata = () => resolve();
        video.onerror = () => reject(new Error('Failed to load video'));

        video.src = videoUrl;
      });
    },

    downloadVideo(event) {
      const filename = this.videoUrl.split('/').pop();
      event.target.download = filename;
    },

    async generateGraph() {
      if (!this.companyName) {
        alert("메인 키워드를 입력하세요!");
        return;
      }

      try {
        const response = await fetch("http://125.181.20.252:8888/generate_graph", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ company_name: this.companyName }),
        });

        const data = await response.json();

        if (data.error) {
          console.error("API 오류:", data.error);
          alert("API 호출 중 오류 발생!");
          return;
        }

        this.analysisResult = `중앙 노드: ${data.nodes[0].id}, 주요 키워드: ${data.nodes.slice(1).map(n => n.id).join(", ")}`;

        this.nodes = data.nodes;
        this.links = data.links;
        this.drawGraph();
      } catch (error) {
        console.error("오류:", error);
        alert("서버와 연결 중 오류 발생!");
      }
    },

    drawGraph() {
      const svg = d3.select("#graphSvg");
      svg.selectAll("*").remove();

      const width = svg.node().getBoundingClientRect().width;
      const height = svg.node().getBoundingClientRect().height;

      const simulation = d3.forceSimulation(this.nodes)
          .force("link", d3.forceLink(this.links).id(d => d.id).distance(50))
          .force("charge", d3.forceManyBody().strength(-100))
          .force("center", d3.forceCenter(width / 2, height / 2));

      const g = svg.append("g");

      const link = g.append("g")
          .selectAll("line")
          .data(this.links)
          .enter().append("line")
          .style("stroke", "#aaa")
          .style("stroke-width", 1.5);

      const nodeGroup = g.append("g")
          .selectAll("g")
          .data(this.nodes)
          .enter().append("g")
          .call(d3.drag()
              .on("start", (event, d) => {
                if (!event.active) simulation.alphaTarget(0.3).restart();
                d.fx = d.x;
                d.fy = d.y;
              })
              .on("drag", (event, d) => {
                d.fx = event.x;
                d.fy = event.y;
              })
              .on("end", (event, d) => {
                if (!event.active) simulation.alphaTarget(0);
                d.fx = null;
                d.fy = null;
              })
          )
          .on("click", this.expandNode);

      nodeGroup.append("circle")
          .attr("r", 10)
          .attr("fill", "#6a5acd");

      nodeGroup.append("text")
          .attr("dy", -15)
          .attr("text-anchor", "middle")
          .attr("fill", "#333")
          .style("font-size", "12px")
          .text(d => d.id);

      simulation.on("tick", () => {
        link
            .attr("x1", d => d.source.x)
            .attr("y1", d => d.source.y)
            .attr("x2", d => d.target.x)
            .attr("y2", d => d.target.y);

        nodeGroup.attr("transform", d => `translate(${d.x},${d.y})`);
      });

      simulation.on("end", () => {
        this.fitGraphToContainer(g, svg);
      });
    },

    fitGraphToContainer(g, svg) {
      const bbox = g.node().getBBox();
      const width = svg.node().getBoundingClientRect().width;
      const height = svg.node().getBoundingClientRect().height;
      const scale = Math.min(width / bbox.width, height / bbox.height, 1) * 0.9;
      const translateX = (width - bbox.width * scale) / 2 - bbox.x * scale;
      const translateY = (height - bbox.height * scale) / 2 - bbox.y * scale;
      g.attr("transform", `translate(${translateX},${translateY}) scale(${scale})`);
    },

    async expandNode(event, d) {
      try {
        const response = await fetch("http://125.181.20.252:8888/expand_node", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ parent_node: d.id }),
        });

        const data = await response.json();

        if (data.error) {
          console.error("서버 응답 오류:", data);
          alert(`상세 오류: ${data.details}`);
          return;
        }

        if (!data.nodes || !data.links) {
          throw new Error("잘못된 데이터 형식");
        }
        this.nodes.push(...data.nodes);
        this.links.push(...data.links);
        this.drawGraph();
      } catch (error) {
        console.error("클라이언트 처리 오류:", error);
        alert(`확장 실패: ${error.message}`);
      }
    },
  },
  async mounted() {
    if (this.$refs.pathSettings) {
      await this.$refs.pathSettings.initializePath();
    }
    window.addEventListener('resize', this.drawGraph);
  },
  beforeUnmount() {
    window.removeEventListener('resize', this.drawGraph);
  }
}
</script>

<style scoped>
.preview-container {
  background-color: var(--background-secondary);
  border-radius: 8px;
  box-shadow: 0 2px 4px var(--shadow-color);
  padding: 20px;
  margin-top: 2rem;
}

.content-layout {
  display: flex;
  gap: 20px;
}

.left-column {
  flex: 0 0 55%;
  width: 60%;
}

.right-column {
  flex: 0 0 40%;
  width: 40%;
}

.video-container,
.analysis-section {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
}

.videos-grid {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
}

.video-cell {
  flex-grow: 1;
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: var(--background-primary);
  border-radius: 8px;
  overflow: hidden;
  width: 85%;
  margin: 0 auto;
}

.video-cell .loading {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.video-cell .output-video {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.download-link {
  position: absolute;
  bottom: 10px;
  left: 50%;
  transform: translateX(-50%);
  background-color: var(--background-secondary);
  padding: 5px 10px;
  border-radius: 4px;
  z-index: 1;
  color: var(--accent-primary);
  text-decoration: none;
}

.download-link:hover {
  text-decoration: underline;
}

.error-message {
  color: var(--error-color);
  margin-top: 10px;
  padding: 10px;
  border-radius: 4px;
  background-color: var(--error-background, #fff3f3);
}

.graph-container {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  background: #f9f9f9;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 20px;
}

.graph-title {
  margin-bottom: 15px;
  color: #344767;
  font-size: 1.2rem;
  font-weight: bold;
}

.input-group {
  display: flex;
  margin-bottom: 5px;
  gap: 10px;
  height: 70%;
}

.input-group .form-control {
  flex: 1;
  border-radius: 5px !important;
  border: 1px solid #ced4da;
  height: 100%;
  font-size: 0.9rem;
}

.input-group .btn {
  background-color: #5CB494;
  border-color: #5CB494;
  border-radius: 5px !important;
  box-shadow: none;
  margin: 0px;
  height: 100%;
  font-size: 0.9rem;
  padding: 0.25rem 0.5rem;
}

.input-group .form-control:focus {
  border-color: #5CB494;
  box-shadow: 0 0 0 0.2rem rgba(92, 180, 148, 0.25);
}

.input-group .btn:hover {
  background-color: #4a9d7c;
  border-color: #4a9d7c;
}

.graph-wrapper {
  flex-grow: 1;
  width: 100%;
  height: 300px;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
}

#graphSvg {
  width: 100%;
  height: 100%;
}

.result-container {
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 10px;
}

.graph-title,
.result-title {
  color: #344767;
  font-size: 1.1rem;
  font-weight: bold;
  margin-bottom: 10px;
}

@media (max-width: 768px) {
  .content-layout {
    flex-direction: column;
  }

  .left-column,
  .right-column {
    flex: 0 0 100%;
    width: 100%;
  }

  .video-cell,
  .graph-container {
    height: 50vh;
  }
}
</style>
<template>
  <div class="container py-7 preview-container">
    <Header />

    <!-- 광고 노출 방법 모델 영역 -->
    <div class="text-center mb-7">
      <p style="color: #344767; font-size: 1.25rem; font-weight: bold;">최적 노출 시간대는 20:00로 도출되었습니다.</p>
      <p style="color: #344767; font-size: 1.25rem; font-weight: bold;">효과 극대화를 위해 배너 광고를 추천드립니다.</p>
    </div>

    <!-- 비디오 출력 영역 -->
    <div>
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

    <!-- 광고 키워드 그래프 영역 -->
    <div class="text-center mb-5">
      <p style="color: #344767; font-size: 1.25rem; font-weight: bold;">관련 키워드 분석</p>
    </div>

    <div class="content-layout d-flex flex-wrap">
      <div class="analysis-section flex-grow-1 d-flex flex-column">
        <div class="graph-container mb-4">
          <h3 class="graph-title">연관 키워드 그래프</h3>
          <div class="input-group mb-3">
            <input type="text" placeholder="메인 키워드 입력" v-model="companyName" class="form-control" />
            <button class="btn btn-primary" @click="generateGraph">
              그래프 생성
            </button>
          </div>
          <div class="graph-wrapper">
            <svg id="graphSvg" width="100%" height="500"></svg>
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
</template>

<script>
import Header from "../HomePage/components/Header.vue";
import * as d3 from "d3";

export default {
  name: "ResultPage",
  components: {
    Header,
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
      companyName: "",
      analysisResult: "",
      nodes: [],
      links: [],
    }
  },
  methods: {
    async generateVideo() {
      this.loadingVideo = true;
      this.errorMessage = '';

      try {
        const requestData = {
          prompt: "광고 영상을 위한 프롬프트",
          useRandomSeed: true,
          frameLength: 16,
          width: 512,
          height: 512,
          enableUpscale: false,
          savePath: this.savePath.fullPath
        };

        const response = await fetch(`${this.serverUrl}/generate`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(requestData)
        });

        if (!response.ok) {
          throw new Error('Failed to generate video');
        }

        const data = await response.json();
        if (!data.success) {
          throw new Error(data.error || 'Video generation failed');
        }

        this.videoUrl = `${this.serverUrl}/output/${this.savePath.userId}/${data.folder}/${data.filename}`;
        await this.waitForVideoLoad(this.videoUrl);

      } catch (error) {
        console.error('Error generating video:', error);
        this.errorMessage = `Error generating video: ${error.message}`;
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
        const response = await fetch("http://127.0.0.1:5001/generate_graph", {
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
      const height = +svg.attr("height");

      const simulation = d3.forceSimulation(this.nodes)
        .force("link", d3.forceLink(this.links).id(d => d.id).distance(100))
        .force("charge", d3.forceManyBody().strength(-200))
        .force("center", d3.forceCenter(width / 2, height / 2));

      const link = svg.append("g")
        .selectAll("line")
        .data(this.links)
        .enter().append("line")
        .style("stroke", "#aaa")
        .style("stroke-width", 1.5);

      const nodeGroup = svg.append("g")
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
    },

    async expandNode(event, d) {
      try {
        const response = await fetch("http://127.0.0.1:5001/expand_node", {
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
  mounted() {
    this.generateVideo();
  }
}
</script>

<style scoped>
.preview-container {
  background-color: var(--background-secondary);
  border-radius: 8px;
  box-shadow: 0 2px 4px var(--shadow-color);
  padding: 20px;
  margin-bottom: 20px;
  margin-top: 100px
}

.videos-grid {
  display: grid;
  gap: 20px;
  width: 100%;
  margin-bottom: 8rem;
}

.videos-grid.single {
  grid-template-columns: 1fr;
}

.video-cell {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: var(--background-primary);
  border-radius: 8px;
  overflow: hidden;
  aspect-ratio: 16/9;
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

.content-layout {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.analysis-section {
  flex: 1;
  min-width: 300px;
}

.graph-container {
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
  margin-bottom: 15px;
  gap: 10px;
}

.input-group .form-control {
  flex: 1;
  border-radius: 5px !important;
  border: 1px solid #ced4da;
}

.input-group .form-control:focus {
  border-color: #5CB494;
  box-shadow: 0 0 0 0.2rem rgba(92, 180, 148, 0.25);
}

.input-group .btn {
  background-color: #5CB494;
  border-color: #5CB494;
  border-radius: 5px !important;
  box-shadow: none;
  margin: 0px;
  font-size: 1rem;
}

.input-group .btn:hover {
  background-color: #4a9d7c;
  border-color: #4a9d7c;
}

.graph-wrapper {
  width: 100%;
  height: 500px;
  overflow: hidden;
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
</style>

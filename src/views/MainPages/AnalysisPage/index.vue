<template>
  <div class="evaluation-page">
    <Header />
    <div class="container py-5">
      <div class="breadcrumb-container mb-4">
        <nav aria-label="breadcrumb">
          <ol class="breadcrumb">
            <li class="breadcrumb-item">
              <router-link to="/">홈</router-link>
            </li>
            <li class="breadcrumb-item active" aria-current="page">
              분석 결과
            </li>
          </ol>
        </nav>
        <h2 class="section-title">분석 결과 및 그래프</h2>
      </div>

      <div class="content-layout d-flex flex-wrap">
        <!-- 영상 영역 -->
        <div class="video-section flex-grow-1">
          <div class="video-container">
            <video controls :src="videoUrl" class="video-player"></video>
          </div>
        </div>

        <!-- 그래프 및 결과 영역 -->
        <div class="analysis-section flex-grow-2 d-flex flex-column">
          <div class="graph-container mb-4">
            <h3 class="graph-title">연관 키워드 그래프</h3>
            <div class="graph-wrapper">
              <input
                type="text"
                id="companyInput"
                placeholder="회사 이름 입력"
                v-model="companyName"
                class="form-control mb-2"
              />
              <button class="btn btn-primary" @click="generateGraph">
                그래프 생성
              </button>
              <svg id="graphSvg" width="100%" height="400"></svg>
            </div>
          </div>

          <div class="result-container">
            <h3 class="result-title">분석 결과</h3>
            <p v-if="analysisResult" class="result-text">
              {{ analysisResult }}
            </p>
            <p v-else class="text-muted">분석 결과가 없습니다.</p>
          </div>
        </div>
      </div>
    </div>

    <footer class="footer">
      <p>Copyright © 2025 Analysis Platform. All rights reserved.</p>
    </footer>
  </div>
</template>

<script>
import Header from "../HomePage/components/Header.vue";
//import * as d3 from "d3";

export default {
  name: "AnalysisPage",
  components: {
    Header,
  },
  data() {
    return {
      companyName: "",
      videoUrl: "https://via.placeholder.com/1280x720.mp4",
      analysisResult: "",
    };
  },
  methods: {
    async generateGraph() {
      if (!this.companyName) {
        alert("회사 이름을 입력하세요!");
        return;
      }

      try {
        const response = await fetch("http://121.176.214.36/v1/workflows/run", {
          method: "POST",
          headers: {
            Authorization: "Bearer app-SqbmU18QN4MXFnvju8slv6ub",
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            inputs: { input: this.companyName },
            response_mode: "blocking",
            user: "example_user",
          }),
        });

        const data = await response.json();
        const text = data?.data?.outputs?.text;

        if (text) {
          const parsedData = JSON.parse(text);
          this.analysisResult = `중앙 노드: ${
            parsedData.central_node
          }, 주요 키워드: ${parsedData.primary_keywords
            .map((k) => k.keyword)
            .join(", ")}`;
          this.drawGraph(parsedData);
        } else {
          console.error("API 호출 오류: 잘못된 응답 데이터", data);
          alert("API 호출 중 문제가 발생했습니다.");
        }
      } catch (error) {
        console.error("오류:", error);
        alert("서버와 연결 중 오류 발생!");
      }
    },
    drawGraph(outputs) {
      const centralNode = outputs.central_node || "Unknown";
      const primaryKeywords = outputs.primary_keywords;

      const nodes = [{ id: centralNode, group: 1 }];
      const links = [];

      primaryKeywords.forEach((primary) => {
        const primaryId = primary.keyword || "Unknown Keyword";
        nodes.push({ id: primaryId, group: 2 });
        links.push({ source: centralNode, target: primaryId });

        if (Array.isArray(primary.sub_keywords)) {
          primary.sub_keywords.forEach((subKeyword) => {
            nodes.push({ id: subKeyword, group: 3 });
            links.push({ source: primaryId, target: subKeyword });
          });
        }
      });

      const svg = d3.select("#graphSvg");
      svg.selectAll("*").remove(); // 기존 그래프 초기화
      const width = svg.node().getBoundingClientRect().width;
      const height = +svg.attr("height");

      const simulation = d3
        .forceSimulation(nodes)
        .force(
          "link",
          d3
            .forceLink(links)
            .id((d) => d.id)
            .distance(100) // 노드 간 거리
        )
        .force("charge", d3.forceManyBody().strength(-200))
        .force("center", d3.forceCenter(width / 2, height / 2)) // 중심에 배치
        .force("collision", d3.forceCollide().radius(30)); // 노드 간 충돌 방지

      const link = svg
        .append("g")
        .selectAll("line")
        .data(links)
        .enter()
        .append("line")
        .attr("class", "link")
        .style("stroke", "#aaa")
        .style("stroke-width", 1.5);

      const node = svg
        .append("g")
        .selectAll("g")
        .data(nodes)
        .enter()
        .append("g")
        .attr("class", "node")
        .call(
          d3
            .drag()
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
        );

      node
        .append("circle")
        .attr("r", (d) => (d.group === 1 ? 15 : 10))
        .attr("fill", (d) => {
          if (d.group === 1) return "#69b3a2";
          if (d.group === 2) return "#6a5acd";
          return "#ff6347";
        });

      node
        .append("text")
        .attr("dy", -15)
        .text((d) => d.id);

      simulation.on("tick", () => {
        link
          .attr("x1", (d) => d.source.x)
          .attr("y1", (d) => d.source.y)
          .attr("x2", (d) => d.target.x)
          .attr("y2", (d) => d.target.y);

        node.attr("transform", (d) => `translate(${d.x},${d.y})`);
      });
    },
  },
};
</script>

<style scoped>
.content-layout {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}
.video-section {
  flex: 1;
  min-width: 300px;
}
.video-player {
  width: 100%;
  height: 300px;
}
.analysis-section {
  flex: 2;
  min-width: 300px;
}
.graph-container {
  background: #f9f9f9;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 10px;
}
.result-container {
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 10px;
}
</style>

<template>
  <div class="analysis-page">
    <Header />
    <div class="container">
      <h1 class="page-title">관련 키워드 분석</h1>
      <div class="analysis-section">
        <div class="graph-container mb-3">
          <h3 class="graph-title">기업 이름 또는 광고 상품 입력</h3>
          <div class="input-group">
            <input type="text" placeholder="메인 키워드 입력" v-model="companyName" class="form-control" />
            <button class="btn btn-primary" @click="generateGraph">
              키워드 보기
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
</template>

<script>
import Header from "../HomePage/components/Header.vue";
import * as d3 from 'd3';

export default {
  name: 'AnalysisPage',
  components: {
    Header,
  },
  data() {
    return {
      companyName: '',
      analysisResult: '',
      nodes: [],
      links: [],
    };
  },
  methods: {
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
        .force("link", d3.forceLink(this.links).id(d => d.id).distance(100))
        .force("charge", d3.forceManyBody().strength(-300))
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
      const scale = Math.min(width / bbox.width, height / bbox.height, 1);
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
  mounted() {
    window.addEventListener('resize', this.drawGraph);
  },
  beforeUnmount() {
    window.removeEventListener('resize', this.drawGraph);
  }
};
</script>

<style scoped>
.analysis-page {
  padding-top: 70px;
  background-color: #f8f9fa;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 5rem;
}

.page-title {
  font-size: 2rem;
  color: #344767;
  margin-bottom: 2rem;
}

.analysis-section {
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 2rem;
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
  height: 70%; /* 높이를 70%로 설정 */
}

.input-group .form-control {
  flex: 1;
  border-radius: 5px !important;
  border: 1px solid #ced4da;
  height: 100%; /* 부모 요소의 높이에 맞춤 */
  font-size: 0.9rem; /* 글자 크기를 약간 줄임 */
}

.input-group .btn {
  background-color: #5CB494;
  border-color: #5CB494;
  border-radius: 5px !important;
  box-shadow: none;
  margin: 0px;
  height: 100%; /* 부모 요소의 높이에 맞춤 */
  font-size: 0.9rem; /* 글자 크기를 약간 줄임 */
  padding: 0.25rem 0.5rem; /* 패딩을 줄여 버튼 내부 여백 조정 */
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
  height: 500px;
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


/* 반응형 디자인을 위한 미디어 쿼리 */
@media (max-width: 768px) {
  .container {
    padding: 1rem;
  }

  .analysis-section {
    padding: 1rem;
  }

  .graph-wrapper {
    height: 50vh;
  }
}
</style>

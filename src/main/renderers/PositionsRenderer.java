package main.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import main.points.PositionBuffer;

public class PositionsRenderer {

  private final PositionBuffer buffer;
  private final ShaderProgram shader;
  private final Mesh mesh;

  public PositionsRenderer(PositionBuffer buffer, ShaderProgram shader) {
    this.shader = shader;
    this.buffer = buffer;
    mesh = new Mesh(false, buffer.positionsArray().length, 0, createAttribute());
  }

  public void render(Matrix4 projection) {
    Gdx.gl20.glEnable(GL20.GL_VERTEX_PROGRAM_POINT_SIZE);
    shader.begin();
    shader.setUniformMatrix("u_projModelView", projection);
    mesh.setVertices(buffer.positionsArray(), 0, buffer.positionsArray().length);
    mesh.render(shader, GL20.GL_POINTS);
    shader.end();
    Gdx.gl20.glDisable(GL20.GL_VERTEX_PROGRAM_POINT_SIZE);
  }

  private static VertexAttribute createAttribute() {
    return new VertexAttribute(1, 2, "a_position");
  }

}

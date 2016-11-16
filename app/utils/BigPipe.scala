package utils

import ui.HtmlStream
import utils.PageLetRenderOptions._


/**
 * This class composes the given PageLets together and prepares them for either out of order client side rendering(if render option is set to ClientSide) or
 * in order server side rendering (if render option is set to ServerSide). Use the render method in this class in your templates to actually render the
 * PageLets. It provides you a Map from PageLet id to the HtmlStream for that PageLet. Insert the HtmlStream in this Map for PageLet into the appropriate
 * part of your markup
 * @param renderOptions
 * @param pageLets
 */




class BigPipe(renderOptions: PageLetRenderOptions, pageLets: PageLet*) {

  /**
   * Render the PageLets in this BigPipe. The layoutBody function will get as an argument a Map from PageLet id to
   * HtmlStream for that PageLet. Insert this HtmlStream into the appropriate place in your markup
   * @param layout
   * @return
   */

  def render(layout: Map[String, HtmlStream] => HtmlStream): HtmlStream = {
    val bodyPageLets = pageLets.map { pageLet =>
      renderOptions match {
        case ClientSide => pageLet.id -> pageLet.renderPlaceholder
        case ServerSide => pageLet.id -> pageLet.renderServerSide
      }
    }.toMap

    val footerPageLets = renderOptions match {
      case ClientSide => HtmlStream.interleave(pageLets.map(_.renderClientSide):_*)
      case ServerSide => HtmlStream.empty
    }

    layout(bodyPageLets).andThen(footerPageLets)

  }

}

/*
 * Convert Markdown to Html
 */

const fs = require('fs')
const { marked } = require('marked')

const ws = (function (src, dest) {
  return {
    src,
    dest,
    srcPath: function (file) { return src + '/' + file },
    destPath: function (file) { return dest + '/' + file },
    isMarkdown: function (file) { return /\.md$/.exec(file) },
    markdownToHtml: function (file) { return file.replace(/\.md$/, '.html') }
  }
})('markdown', 'html')

fs.mkdirSync(ws.dest, { recursive: true })
fs.readdirSync(ws.src)
  .filter(function (file) {
    return ws.isMarkdown(ws.srcPath(file))
  })
  .forEach(function (file) {
    console.log('Converting ' + ws.srcPath(file) + ' to HTML ' + ws.destPath(file))

    // Replace links
    const data = fs.readFileSync(ws.srcPath(file), 'utf8').replace(/\.md/g, '.html')
    fs.writeFileSync(ws.markdownToHtml(ws.destPath(file)), marked.parse(data))
  })
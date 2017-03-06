import {Component, OnInit, ElementRef, Inject, ViewChild} from '@angular/core';
import {TrainingFilesListSelectorService} from "../training-files-list-selector/training-files-list-selector.service";
import {Http} from "@angular/http";
import {DOCUMENT, DomSanitizer, SafeHtml} from '@angular/platform-browser';
import {PageScrollService, PageScrollInstance, PageScrollConfig} from 'ng2-page-scroll';
import {MdDialog, MdDialogConfig} from "@angular/material";
import {SimpleDialog} from "../../tools/simple-dialog/simple-dialog.component";

@Component({
  selector: 'training-file-editor',
  templateUrl: './training-file-editor.component.html',
  styleUrls: ['./training-file-editor.component.scss']
})
export class TrainingFileEditorComponent implements OnInit {

  tei: string;
  teiHtml: string;
  teiHtmlTrust: any;
  groups = [];
  currentSelection: any = null;
  currentRange: any = null;
  currentFileName: string = null;
  currentObjectNodes: Array<Element> = [];
  toolsStyle: any = {};

  history: any = [];
  saved: boolean = true;

  @ViewChild('editorRef')
  private editorRef: ElementRef;

  @ViewChild('contentRef')
  private contentRef: ElementRef;

  constructor(private http: Http, private filesListService: TrainingFilesListSelectorService, @Inject(DOCUMENT) private document: Document, private pageScrollService: PageScrollService, private sanitized: DomSanitizer, public dialog: MdDialog) {
    this.onFileSelected = this.onFileSelected.bind(this);
    filesListService.onFileSelected(this.onFileSelected);

    PageScrollConfig.defaultScrollOffset = (window.innerHeight-64)/2;
    PageScrollConfig.defaultDuration = 400;
  }

  private static newGUIDString(): string {
    let d = new Date().getTime();
    if (window.performance && typeof window.performance.now === "function") {
      d += performance.now();
    }
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) => {
      let r = (d + Math.random() * 16) % 16 | 0;
      d = Math.floor(d/16);
      return (c=='x' ? r : (r & 0x3 | 0x8)).toString(16);
    });
  }

  ngOnInit() {
  }

  newObject(raw, doc, isNew: boolean) {
    var newNode = doc.createElement('span');
    let id = 'obj-ref-'+TrainingFileEditorComponent.newGUIDString();
    let textNode = doc.createTextNode(raw);
    newNode.appendChild(textNode);
    newNode.setAttribute('class', 'obj-ref');
    newNode.id = id;

    let obj = {
      'raw': raw,
      'id': id,
      'isNew': isNew
    };

    var found = false;
    for (var i=0 ; i<this.groups.length ; i++) {
      if (this.groups[i].raw.toLowerCase() == obj.raw.toLowerCase()) {
        this.groups[i].objects.push(obj);
        found = true;
        break;
      }
    }

    if (!found) {
      let group = {
        'raw': obj.raw,
        'objects': [obj]
      }
      this.groups.push(group);
    }

    return newNode;
  }

  onFileSelected(filename: string) {

    if (this.saved) {
      this.selectFile(filename);
    }
    else {
      let dialogRef = this.dialog.open(SimpleDialog);
      dialogRef.componentInstance.message = 'You don\'t saved file, are you sure to erase last changes ?';
      dialogRef.componentInstance.yes = 'Yes';
      dialogRef.componentInstance.no = 'Cancel';
      dialogRef.afterClosed().subscribe(result => {
        if (result === true) {
          this.selectFile(filename);
        }
      });
    }
  }

  selectFile(filename: string) {
    this.currentSelection = null;
    this.currentFileName = filename;

    this.editorRef.nativeElement.scrollTop = 0;

    this.teiHtml = '';
    this.teiHtmlTrust = '';
    this.groups = [];
    this.history = [];
    this.saved = true;

    this.http.get('/api/training/file?name='+encodeURIComponent(filename))
      .subscribe
      (data => {

        this.tei = data.json().content;

        this.teiHtml = this.teiToHtml(this.tei);

        this.teiHtmlTrust = this.sanitized.bypassSecurityTrustHtml(this.teiHtml);

      }, error => {

        console.error(error);

      });
  }

  teiToHtml(data: string) {
    let parser = new DOMParser();
    let teiDoc = parser.parseFromString(data, 'text/xml');

    return this.renderNodes(teiDoc, teiDoc, 'html');
  }

  htmlToTei(data: string) {
    let parser = new DOMParser();
    let htmlDoc = parser.parseFromString(data, 'text/html');

    var tei = this.renderNodes(htmlDoc, htmlDoc, 'tei');

    tei = '<?xml version="1.0"?>\n<tei xmlns="http://www.tei-c.org/ns/1.0">\n\t<text xml:lang="en">\n\n'
      + tei
      + '</text></tei>';

    return tei;
  }

  renderNodes(node, doc, type) {
    var h_src = 'head';
    var p_src = 'p';
    var h_to = 'h3';
    var p_to = 'p';

    if (type == 'tei') {
      h_src = 'H3';
      p_src = 'P';
      h_to = 'head';
      p_to = 'p';
    }

    var res = '';
    if (node.childNodes.length > 0) {

      /*var hasDoubleTitles = false;
      var previousContent = null;*/
      for (var i = 0; i < node.childNodes.length; i++) {

        // check if double title ! (Hack, should not be kept)
        /*var content = '';
        var noPrint = false;
        if ((node.childNodes[i].nodeName == h_src) || (node.childNodes[i].nodeName == p_to)) {
          content = node.childNodes[i].textContent;

          if (previousContent == content) {
            hasDoubleTitles = true;
            noPrint = true;
          }

          if (node.childNodes[i].nodeName == h_src) {
            previousContent = content;
          }
          else {
            previousContent = null;
          }
        }*/

        //if (!noPrint) {
          switch (node.childNodes[i].nodeName) {
            case h_src:
              res += '\t\t<' + h_to + '>' + this.renderInner(node.childNodes[i], doc, type) + '</' + h_to + '>\n\n';
              break;
            case p_src:
              res += '\t\t<' + p_to + '>' + this.renderInner(node.childNodes[i], doc, type) + '</' + p_to + '>\n\n';
              break;
            default:
              res += this.renderNodes(node.childNodes[i], doc, type);
              break;
          }
        //}
      }

      /*if (hasDoubleTitles) {
        this.pushState();
      }*/
    }
    return res;
  }

  renderInner(node, doc, type) {
    var obj_tag = 'rs';
    if (type == 'tei')
      obj_tag = 'SPAN';

    for (var i = 0; i < node.childNodes.length; i++) {
      if (node.childNodes[i].nodeName == obj_tag) {
        let raw = node.childNodes[i].textContent;

        var newNode = null;

        if (type == 'tei') {
          if ((node.childNodes[i] as Element).getAttribute('class').indexOf('obj-ref') != -1) {
            newNode = doc.createElement('rs');
            let textNode = doc.createTextNode(raw);
            newNode.appendChild(textNode);
            newNode.setAttribute('type', 'astro-object');
          }
        }
        else {
          newNode = this.newObject(raw, doc, false);
        }

        node.replaceChild(newNode, node.childNodes[i]);
      }
    }
    return node.innerHTML;//this.cleanMNRAS(node.innerHTML);
  }

  /*cleanMNRAS(content: string) {
    let firstIndex = content.indexOf('at Observatoire Astronomique de Strasbourg on');

    if (firstIndex == -1)
      return content;

    let endSearch = 'http://mnras.oxfordjournals.org/Downloaded from';
    let lastIndex = content.indexOf(endSearch, firstIndex) + endSearch.length;

    if (lastIndex == -1)
      return content;

    let res = content.substring(0, firstIndex) + content.substring(lastIndex);
    console.log('MNRAS '+firstIndex+' '+lastIndex+'\n'+res);
    this.saved = false;

    return res;
  }*/

  pushState() {
    this.saved = false;

    var groups = [];
    for (var i=0 ;i<this.groups.length ; i++) {
      groups.push({
        'raw': this.groups[i].raw,
        'objects': this.groups[i].objects.slice(0)
      });
    }

    var state = {
      'teiHtmlTrust': this.sanitized.bypassSecurityTrustHtml(this.contentRef.nativeElement.innerHTML),
      'groups': groups
    };

    this.history.push(state);
  }

  popState() {
    var state = this.history.pop();
    this.teiHtmlTrust = state.teiHtmlTrust;
    this.groups = state.groups;
  }

  save() {
    this.saved = true;

    var html = this.htmlToTei(this.contentRef.nativeElement.innerHTML);

    if (html != '') {
      this.http.post('/api/training/file', {
        'filename': this.currentFileName,
        'tei': html
      })
        .subscribe
        (data => {

          console.log('sent ok !');

        }, error => {

          console.error(error);

        });
    }
  }

  anchor(id) {
    let pageScrollInstance: PageScrollInstance = PageScrollInstance.simpleInlineInstance(this.document, '#'+id, this.editorRef.nativeElement);
    this.pageScrollService.start(pageScrollInstance);
    document.getElementById(id).className = 'obj-ref highlighted';
    setTimeout(() => document.getElementById(id).className = 'obj-ref', 500);
  }

  onSelected() {

    if (rangy.getSelection().toString() == '') {
      this.currentSelection = null;
      return;
    }

    this.currentSelection = rangy.getSelection();
    this.currentRange = this.currentSelection.getAllRanges()[0];
    this.currentObjectNodes = [];

    let rect = window.getSelection().getRangeAt(0).getBoundingClientRect();

    this.toolsStyle = {
      'top': (this.editorRef.nativeElement.scrollTop+rect.top-64-50)+'px',
      'left': (rect.left - this.editorRef.nativeElement.getBoundingClientRect().left)+'px'
    };

    this.checkSelection();
  }

  checkSelection() {

    let range = this.currentRange;
    let ancestor = range.commonAncestorContainer;

    if (ancestor.nodeType == Node.TEXT_NODE)
      ancestor = ancestor.parentNode;

    if (ancestor.nodeName == 'SPAN')
      ancestor = ancestor.parentElement;

    // si selection inter-paragraphe
    if ((ancestor.nodeName != 'P') && (ancestor.nodeName != 'H3')) {
      this.currentSelection = null;
      return;
    }

    this.currentObjectNodes = this.objectParser(ancestor, range.startContainer, range.endContainer);
  }

  objectParser(node: Node, startNode: Node, endNode: Node): Array<Element> {

    var objectNodes: Array<Element> = [];
    var parseStarted = false;

    if (startNode == null)
      parseStarted = true;

    for (var i=0 ; i<node.childNodes.length ; i++) {
      let curNode = node.childNodes[i];

      if (curNode == startNode)
        parseStarted = true;

      if (startNode != null)
        if (curNode == startNode.parentNode)
          parseStarted = true;

      // check si possède déjà un objet
      if (parseStarted) {
        if (curNode instanceof Element) {
          let element: Element = curNode as Element;
          if (element.nodeName == 'SPAN') {
            if (element.getAttribute('class').indexOf('obj-ref') != -1) {
              objectNodes.push(element);
            }
          }
        }
      }

      if (curNode == endNode)
        break;

      if (endNode != null)
        if (curNode == endNode.parentNode)
          break;
    }

    return objectNodes;
  }

  selDeleteParagraph() {
    this.pushState();

    let range = this.currentRange;
    let ancestor = range.commonAncestorContainer;

    let pragraphNode: Element = this.getParagraphNode(ancestor);

    let objectNodes: Array<Element> = this.objectParser(pragraphNode, null, null);

    this.removeObjects(objectNodes);

    pragraphNode.parentNode.removeChild(pragraphNode);

    this.currentSelection = null;
  }

  getParagraphNode(node: Node): Element {
    if (node != null) {
      if ((node.nodeName == 'P') || (node.nodeName == 'H3')) {
        return node as Element;
      }
      else {
        return this.getParagraphNode(node.parentNode);
      }
    }
  }

  selAddObject() {

    this.pushState();

    let range = this.currentRange;
    let ancestor = range.commonAncestorContainer;

    if (ancestor.nodeType != Node.TEXT_NODE) {
      console.error('ancestor.nodeType != Node.TEXT_NODE');
      return;
    }

    let parentElement: Element = ancestor.parentElement;

    let ancestorText = ancestor as Text;
    let splitText2: Text = ancestorText.splitText(range.startOffset);
    let splitText3: Text = splitText2.splitText(this.currentSelection.toString().length);

    let newNode = this.newObject(this.currentSelection.toString(), document, true);

    parentElement.replaceChild(newNode, splitText2);

    this.currentSelection = null;
  }

  removeObjects(objectNodes: Array<Element>) {

    for (var i=0 ; i<objectNodes.length ; i++) {
      let curNode: Element = objectNodes[i];

      var raw = '-!-error-!-';
      var found = false;
      for (var g=0 ; g<this.groups.length ; g++) {
        var group = this.groups[g];
        for (var o=0 ; o<group.objects.length ; o++) {
          if (group.objects[o].id == curNode.id) {
            raw = group.objects[o].raw;
            group.objects.splice(o, 1);
            if (group.objects.length == 0)
              this.groups.splice(g, 1);
            found = true;
            break;
          }
        }
        if (found)
          break;
      }


      var textNode: Text = document.createTextNode(raw);

      curNode.parentElement.replaceChild(textNode, curNode);
    }
  }

  selRemoveObjects() {

    this.pushState();

    this.removeObjects(this.currentObjectNodes);

    this.currentSelection = null;
  }

  selCropObjects() {

    this.pushState();

    let range:RangyRange = this.currentRange;
    var ancestor = range.startContainer;
    while ((ancestor.nodeName != 'P') && (ancestor.nodeName != 'H3'))
      ancestor = ancestor.parentNode;
    let raw = this.currentSelection.toString();

    var startNode = range.startContainer;
    while (Array.prototype.indexOf.call(ancestor.childNodes, startNode) == -1)
      startNode = startNode.parentNode;
    let startNodeIndex = Array.prototype.indexOf.call(ancestor.childNodes, startNode);

    var endNode = range.endContainer;
    while (Array.prototype.indexOf.call(ancestor.childNodes, endNode) == -1)
      endNode = endNode.parentNode;
    let endNodeIndex = Array.prototype.indexOf.call(ancestor.childNodes, endNode);


    this.removeObjects(this.currentObjectNodes);

    startNode = ancestor.childNodes[startNodeIndex];
    endNode = ancestor.childNodes[endNodeIndex];

    var betweenNodes: Array<Node> = [];
    for (var i=startNodeIndex+1 ; i<=endNodeIndex-1 ; i++)
      betweenNodes.push(ancestor.childNodes[i]);
    for (var j=0 ; j<betweenNodes.length ; j++)
      ancestor.removeChild(betweenNodes[j]);


    let newNode = this.newObject(raw, document, true);

    if (startNode != endNode) {
      let splitStart: Text = (startNode as Text).splitText(range.startOffset);
      ancestor.removeChild(splitStart);

      let splitEnd: Text = (endNode as Text).splitText(range.endOffset);
      ancestor.removeChild(endNode);

      ancestor.insertBefore(newNode, splitEnd);
    }
    else {
      let middle: Text = (startNode as Text).splitText(range.startOffset);

      let splitEnd: Text = (middle as Text).splitText(range.endOffset - range.startOffset);

      ancestor.replaceChild(newNode, middle);
    }

    this.currentSelection = null;
  }

}

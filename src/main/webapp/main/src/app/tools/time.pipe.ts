import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'time'
})
export class TimePipe implements PipeTransform {

  transform(value: number, args?: any): any {

    var ms = value % 1000;
    value = (value - ms) / 1000;
    var secs = value % 60;
    value = (value - secs) / 60;
    var mins = value % 60;
    var hrs = (value - mins) / 60;

    return ( hrs > 0 ? this.pad(hrs) + '<small>h</small> ' : '')
      + ( mins > 0 ? this.pad(mins) + '<small>min</small> ' : '')
      + ( mins > 0 ? this.pad(secs) + '<small>s</small> ' : '')
      + ms + '<small>ms</small>';
  }

  pad(n){
    return n<10 ? '0'+n : n
  }

}
